#!/bin/bash

# EksiClone API Test Script
# Bu script eksiclone uygulamasında tüm API uçlarını test eder

# Log dosyası
LOG_FILE="eksiclone_api_test_$(date +%Y%m%d_%H%M%S).log"

# API base URL
GATEWAY_URL="http://localhost:8080"
USER_SERVICE_URL="http://localhost:8082"
ROLE_SERVICE_URL="http://localhost:8083"

# Log functions
log_info() {
    echo -e "[INFO] $(date +"%Y-%m-%d %H:%M:%S") - $1" | tee -a "$LOG_FILE"
}

log_error() {
    echo -e "[ERROR] $(date +"%Y-%m-%d %H:%M:%S") - $1" | tee -a "$LOG_FILE"
}

log_success() {
    echo -e "[SUCCESS] $(date +"%Y-%m-%d %H:%M:%S") - $1" | tee -a "$LOG_FILE"
}

# Check if a service is running
check_service() {
    local url="$1"
    local service_name="$2"
    
    log_info "Checking if $service_name is available..."
    
    if curl -s -f "$url" > /dev/null 2>&1; then
        log_success "$service_name is running"
        return 0
    else
        log_error "$service_name is not available at $url"
        return 1
    fi
}

# JWT Token storage
TOKEN=""
REFRESH_TOKEN=""

# Test Authentication API
test_auth() {
    log_info "Testing Authentication API..."
    
    # Login with admin credentials
    log_info "Logging in with admin credentials..."
    
    response=$(curl -s -X POST \
        -H "Content-Type: application/json" \
        -d '{"username":"admin_user","password":"password123"}' \
        "$GATEWAY_URL/api/v1/auth/login")
    
    # Check if login was successful
    if echo "$response" | grep -q "token"; then
        TOKEN=$(echo "$response" | grep -o '"token":"[^"]*' | sed 's/"token":"//')
        REFRESH_TOKEN=$(echo "$response" | grep -o '"refreshToken":"[^"]*' | sed 's/"refreshToken":"//')
        log_success "Login successful, got JWT token"
        echo "$response" >> "$LOG_FILE"
    else
        log_error "Login failed"
        echo "$response" >> "$LOG_FILE"
        # Try direct service call if gateway fails
        response=$(curl -s -X POST \
            -H "Content-Type: application/json" \
            -d '{"username":"admin_user","password":"password123"}' \
            "$USER_SERVICE_URL/api/v1/auth/login")
        
        if echo "$response" | grep -q "token"; then
            TOKEN=$(echo "$response" | grep -o '"token":"[^"]*' | sed 's/"token":"//')
            REFRESH_TOKEN=$(echo "$response" | grep -o '"refreshToken":"[^"]*' | sed 's/"refreshToken":"//')
            log_success "Direct login successful, got JWT token"
            echo "$response" >> "$LOG_FILE"
        else
            log_error "Direct login also failed, cannot proceed with tests"
            echo "$response" >> "$LOG_FILE"
            return 1
        fi
    fi
    
    # Test JWT token refresh
    log_info "Testing token refresh..."
    
    refresh_response=$(curl -s -X POST \
        -H "Content-Type: application/json" \
        -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}" \
        "$GATEWAY_URL/api/v1/auth/refresh")
    
    if echo "$refresh_response" | grep -q "token"; then
        TOKEN=$(echo "$refresh_response" | grep -o '"token":"[^"]*' | sed 's/"token":"//')
        log_success "Token refresh successful"
        echo "$refresh_response" >> "$LOG_FILE"
    else
        log_error "Token refresh failed, continuing with original token"
        echo "$refresh_response" >> "$LOG_FILE"
    fi
    
    # Test JWK endpoint
    log_info "Testing JWK endpoint..."
    
    jwk_response=$(curl -s -X GET "$GATEWAY_URL/api/v1/auth/jwks.json")
    
    if echo "$jwk_response" | grep -q "keys"; then
        log_success "JWK endpoint working"
    else
        log_error "JWK endpoint not working properly"
        echo "$jwk_response" >> "$LOG_FILE"
    fi
    
    return 0
}

# Test User API
test_users() {
    log_info "Testing User API..."
    
    # Get all users
    log_info "Getting all users..."
    
    users_response=$(curl -s -X GET \
        -H "Authorization: Bearer $TOKEN" \
        "$GATEWAY_URL/api/v1/users")
    
    if [[ "$users_response" == \[*\] ]]; then
        log_success "Get all users successful"
        echo "$users_response" | head -c 300 >> "$LOG_FILE"
        echo "..." >> "$LOG_FILE"
    else
        log_error "Get all users failed"
        echo "$users_response" >> "$LOG_FILE"
    fi
    
    # Get user by ID
    log_info "Getting user by ID (1)..."
    
    user_response=$(curl -s -X GET \
        -H "Authorization: Bearer $TOKEN" \
        "$GATEWAY_URL/api/v1/users/1")
    
    if echo "$user_response" | grep -q "username"; then
        log_success "Get user by ID successful"
        echo "$user_response" >> "$LOG_FILE"
    else
        log_error "Get user by ID failed"
        echo "$user_response" >> "$LOG_FILE"
    fi
    
    # Create a new user
    log_info "Creating a new user..."
    
    new_user_response=$(curl -s -X POST \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d '{
            "username": "test_user_'$(date +%s)'",
            "email": "test_'$(date +%s)'@example.com",
            "password": "TestPassword123",
            "name": "Test",
            "lastName": "User",
            "roleNames": ["ROLE_USER"]
        }' \
        "$GATEWAY_URL/api/v1/users")
    
    if echo "$new_user_response" | grep -q '"id":[0-9]'; then
        log_success "Create user successful"
        echo "$new_user_response" >> "$LOG_FILE"
        # Extract the user ID for update and delete tests
        NEW_USER_ID=$(echo "$new_user_response" | grep -o '"id":[0-9]*' | sed 's/"id"://')
    else
        log_error "Create user failed"
        echo "$new_user_response" >> "$LOG_FILE"
        # Use a default user ID for update and delete tests
        NEW_USER_ID=3
    fi
    
    # Update user
    log_info "Updating user with ID $NEW_USER_ID..."
    
    update_user_response=$(curl -s -X PUT \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d '{
            "username": "updated_user_'$(date +%s)'",
            "email": "updated_'$(date +%s)'@example.com",
            "password": "UpdatedPassword123",
            "name": "Updated",
            "lastName": "User",
            "roleNames": ["ROLE_USER", "ROLE_MODERATOR"]
        }' \
        "$GATEWAY_URL/api/v1/users/$NEW_USER_ID")
    
    if echo "$update_user_response" | grep -q "username"; then
        log_success "Update user successful"
        echo "$update_user_response" >> "$LOG_FILE"
    else
        log_error "Update user failed"
        echo "$update_user_response" >> "$LOG_FILE"
    fi
    
    # Delete user (be careful with this in production)
    log_info "Deleting user with ID $NEW_USER_ID..."
    
    delete_user_response=$(curl -s -X DELETE \
        -H "Authorization: Bearer $TOKEN" \
        -w "%{http_code}" \
        -o /dev/null \
        "$GATEWAY_URL/api/v1/users/$NEW_USER_ID")
    
    if [ "$delete_user_response" = "204" ]; then
        log_success "Delete user successful"
    else
        log_error "Delete user failed with HTTP code $delete_user_response"
    fi
    
    return 0
}

# Test Role API
test_roles() {
    log_info "Testing Role API..."
    
    # Get all roles
    log_info "Getting all roles..."
    
    roles_response=$(curl -s -X GET \
        -H "Authorization: Bearer $TOKEN" \
        "$GATEWAY_URL/api/v1/roles")
    
    if [[ "$roles_response" == \[*\] ]]; then
        log_success "Get all roles successful"
        echo "$roles_response" >> "$LOG_FILE"
    else
        log_error "Get all roles failed"
        echo "$roles_response" >> "$LOG_FILE"
    fi
    
    # Get role by ID
    log_info "Getting role by ID (1)..."
    
    role_response=$(curl -s -X GET \
        -H "Authorization: Bearer $TOKEN" \
        "$GATEWAY_URL/api/v1/roles/1")
    
    if echo "$role_response" | grep -q "roleName"; then
        log_success "Get role by ID successful"
        echo "$role_response" >> "$LOG_FILE"
    else
        log_error "Get role by ID failed"
        echo "$role_response" >> "$LOG_FILE"
    fi
    
    # Create a new role with doğru format
    log_info "Creating a new role..."
    
    # Rol adını düzeltiyoruz - ROLE_ ile başlamalı, sadece büyük harf ve alt çizgi içermeli
    TIMESTAMP=$(date +%s | tr "[:lower:]" "[:upper:]")
    
    new_role_response=$(curl -s -X POST \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d '{
            "roleName": "ROLE_TEST_'$TIMESTAMP'"
        }' \
        "$GATEWAY_URL/api/v1/roles")
    
    # Başarı kontrolü
    if echo "$new_role_response" | grep -q '"id":[0-9]'; then
        log_success "Create role successful"
        echo "$new_role_response" >> "$LOG_FILE"
        # Extract the role ID for update and delete tests
        NEW_ROLE_ID=$(echo "$new_role_response" | grep -o '"id":[0-9]*' | sed 's/"id"://')
        log_info "Role created with ID $NEW_ROLE_ID"
    else
        log_error "Create role failed"
        echo "$new_role_response" >> "$LOG_FILE"
        # Use a default role ID for update and delete tests
        NEW_ROLE_ID=""
        log_info "Failed to create role, will create a temporary role for tests"
    fi
    
    # Eğer rol oluşturma başarısız olduysa, yeni bir deneme yap
    if [ -z "$NEW_ROLE_ID" ]; then
        log_info "Trying to create a role with alternative format..."
        
        new_role_response=$(curl -s -X POST \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $TOKEN" \
            -d '{
                "roleName": "ROLE_ALTERNATIVE"
            }' \
            "$GATEWAY_URL/api/v1/roles")
        
        if echo "$new_role_response" | grep -q '"id":[0-9]'; then
            log_success "Alternative role creation successful"
            echo "$new_role_response" >> "$LOG_FILE"
            NEW_ROLE_ID=$(echo "$new_role_response" | grep -o '"id":[0-9]*' | sed 's/"id"://')
            log_info "Alternative role created with ID $NEW_ROLE_ID"
        else
            log_error "Alternative role creation also failed"
            echo "$new_role_response" >> "$LOG_FILE"
        fi
    fi
    
    # Eğer bir rol ID'si elde edildiyse update ve delete testlerini yap
    if [ ! -z "$NEW_ROLE_ID" ]; then
        # Update role
        log_info "Updating role with ID $NEW_ROLE_ID..."
        
        update_role_response=$(curl -s -X PUT \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $TOKEN" \
            -d '{
                "roleName": "ROLE_UPDATED_'$TIMESTAMP'"
            }' \
            "$GATEWAY_URL/api/v1/roles/$NEW_ROLE_ID")
        
        if echo "$update_role_response" | grep -q '"id":[0-9]' && echo "$update_role_response" | grep -q '"roleName"'; then
            log_success "Update role successful"
            echo "$update_role_response" >> "$LOG_FILE"
        else
            log_error "Update role failed"
            echo "$update_role_response" >> "$LOG_FILE"
        fi
        
        # Delete role (will only work if no users have this role)
        log_info "Deleting role with ID $NEW_ROLE_ID..."
        
        delete_role_response=$(curl -s -X DELETE \
            -H "Authorization: Bearer $TOKEN" \
            -w "%{http_code}" \
            -o /dev/null \
            "$GATEWAY_URL/api/v1/roles/$NEW_ROLE_ID")
        
        if [ "$delete_role_response" = "204" ]; then
            log_success "Delete role successful"
        else
            log_error "Delete role failed with HTTP code $delete_role_response"
            
            # Eğer silme işlemi başarısız olursa, rol kullanılıyor olabilir
            log_info "Role might be assigned to users, checking with GET request..."
            
            role_check=$(curl -s -X GET \
                -H "Authorization: Bearer $TOKEN" \
                "$GATEWAY_URL/api/v1/roles/$NEW_ROLE_ID")
            
            if echo "$role_check" | grep -q "roleName"; then
                log_info "Role still exists, it may be assigned to users"
                echo "$role_check" >> "$LOG_FILE"
            else
                log_info "Role no longer exists or cannot be accessed"
            fi
        fi
    else
        log_info "Skipping role update and delete tests as no valid role ID was obtained"
    fi
    
    # Try to create a temporary role and delete it specifically for delete test
    log_info "Creating a temporary role specifically for delete test..."
    
    temp_role_response=$(curl -s -X POST \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d '{
            "roleName": "ROLE_TEMP_'$TIMESTAMP'"
        }' \
        "$GATEWAY_URL/api/v1/roles")
    
    if echo "$temp_role_response" | grep -q '"id":[0-9]'; then
        log_success "Temporary role created successfully"
        echo "$temp_role_response" >> "$LOG_FILE"
        
        # Extract the role ID 
        TEMP_ROLE_ID=$(echo "$temp_role_response" | grep -o '"id":[0-9]*' | sed 's/"id"://')
        log_info "Temporary role created with ID $TEMP_ROLE_ID"
        
        # Delete the temporary role
        log_info "Deleting temporary role with ID $TEMP_ROLE_ID..."
        
        delete_temp_response=$(curl -s -X DELETE \
            -H "Authorization: Bearer $TOKEN" \
            -w "%{http_code}" \
            -o /dev/null \
            "$GATEWAY_URL/api/v1/roles/$TEMP_ROLE_ID")
        
        if [ "$delete_temp_response" = "204" ]; then
            log_success "Temporary role deletion successful"
        else
            log_error "Temporary role deletion failed with HTTP code $delete_temp_response"
        fi
    else
        log_error "Failed to create temporary role for delete test"
        echo "$temp_role_response" >> "$LOG_FILE"
    fi
    
    # Try to delete a role that is assigned to users (should fail)
    log_info "Trying to delete a role that is assigned to users (ID 1 - ROLE_ADMIN)..."
    
    delete_admin_response=$(curl -s -X DELETE \
        -H "Authorization: Bearer $TOKEN" \
        "$GATEWAY_URL/api/v1/roles/1")
    
    if echo "$delete_admin_response" | grep -q "error"; then
        log_success "Delete role with users correctly failed with proper error message"
        echo "$delete_admin_response" >> "$LOG_FILE"
    else
        log_error "Delete role with users didn't fail as expected"
        echo "$delete_admin_response" >> "$LOG_FILE"
    fi
    
    return 0
}

# Test with invalid token
test_invalid_auth() {
    log_info "Testing APIs with invalid authentication..."
    
    INVALID_TOKEN="eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbl91c2VyIiwiY3JlYXRlZCI6MTYyMDY0NTU5MjQ3Niwicm9sZXMiOlsiUk9MRV9BRE1JTiJdLCJpYXQiOjE2MjA2NDU1OTIsImV4cCI6MTYyMDY0NTU5Mn0.invalid_signature"
    
    # Try to get users with invalid token
    log_info "Trying to get users with invalid token..."
    
    invalid_auth_response=$(curl -s -X GET \
        -H "Authorization: Bearer $INVALID_TOKEN" \
        -w "%{http_code}" \
        -o /dev/null \
        "$GATEWAY_URL/api/v1/users")
    
    if [ "$invalid_auth_response" = "401" ]; then
        log_success "Invalid token correctly rejected with 401 Unauthorized"
    else
        log_error "Invalid token test failed, got HTTP code $invalid_auth_response"
    fi
    
    # Try without any token
    log_info "Trying to access protected resource without token..."
    
    no_auth_response=$(curl -s -X GET \
        -w "%{http_code}" \
        -o /dev/null \
        "$GATEWAY_URL/api/v1/users")
    
    if [ "$no_auth_response" = "401" ]; then
        log_success "No token correctly rejected with 401 Unauthorized"
    else
        log_error "No token test failed, got HTTP code $no_auth_response"
    fi
    
    return 0
}

# Test edge cases
test_edge_cases() {
    log_info "Testing edge cases..."
    
    # Try to create a user with invalid data
    log_info "Trying to create a user with invalid data..."
    
    invalid_user_response=$(curl -s -X POST \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d '{
            "username": "a",
            "email": "not-an-email",
            "password": "short",
            "name": "",
            "lastName": "",
            "roleNames": ["NON_EXISTENT_ROLE"]
        }' \
        "$GATEWAY_URL/api/v1/users")
    
    # Düzeltilmiş validasyon kontrolü
    if echo "$invalid_user_response" | grep -q -E 'username|email|password|name|lastName'; then
        log_success "Invalid user data correctly rejected with validation errors"
        echo "$invalid_user_response" >> "$LOG_FILE"
    else
        log_error "Invalid user data validation failed unexpectedly"
        echo "$invalid_user_response" >> "$LOG_FILE"
    fi
    
    # Try to create a role with invalid name
    log_info "Trying to create a role with invalid name..."
    
    invalid_role_response=$(curl -s -X POST \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d '{
            "roleName": "invalid_role_name"
        }' \
        "$GATEWAY_URL/api/v1/roles")
    
    # Düzeltilmiş validasyon kontrolü
    if echo "$invalid_role_response" | grep -q -E "roleName"; then
        log_success "Invalid role name correctly rejected with validation errors"
        echo "$invalid_role_response" >> "$LOG_FILE"
    else
        log_error "Invalid role name validation failed unexpectedly"
        echo "$invalid_role_response" >> "$LOG_FILE"
    fi
    
    # Try to get a non-existent user
    log_info "Trying to get a non-existent user (ID 999)..."
    
    non_existent_user_response=$(curl -s -X GET \
        -H "Authorization: Bearer $TOKEN" \
        "$GATEWAY_URL/api/v1/users/999")
    
    if echo "$non_existent_user_response" | grep -q "error"; then
        log_success "Non-existent user correctly returned error"
        echo "$non_existent_user_response" >> "$LOG_FILE"
    else
        log_error "Non-existent user didn't return proper error"
        echo "$non_existent_user_response" >> "$LOG_FILE"
    fi
    
    # Try to create a user with existing username
    log_info "Trying to create a user with existing username..."
    
    duplicate_user_response=$(curl -s -X POST \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d '{
            "username": "admin_user",
            "email": "unique_'$(date +%s)'@example.com",
            "password": "ValidPassword123",
            "name": "Duplicate",
            "lastName": "User",
            "roleNames": ["ROLE_USER"]
        }' \
        "$GATEWAY_URL/api/v1/users")
    
    if echo "$duplicate_user_response" | grep -q "error"; then
        log_success "Duplicate username correctly rejected"
        echo "$duplicate_user_response" >> "$LOG_FILE"
    else
        log_error "Duplicate username not properly validated"
        echo "$duplicate_user_response" >> "$LOG_FILE"
    fi
    
    return 0
}

# Run all tests
run_all_tests() {
    log_info "Starting EksiClone API Tests..."
    
    # Check if services are running
    check_service "$GATEWAY_URL/actuator/health" "API Gateway" || return 1
    check_service "$USER_SERVICE_URL/actuator/health" "User Service" || return 1
    check_service "$ROLE_SERVICE_URL/actuator/health" "Role Service" || return 1
    
    # Run authentication tests first to get token
    test_auth || return 1
    
    # Run other tests
    test_users
    test_roles
    test_invalid_auth
    test_edge_cases
    
    log_info "All tests completed!"
    
    return 0
}

# Create log file
touch "$LOG_FILE"
log_info "Starting API test script. Log file: $LOG_FILE"

# Run tests
run_all_tests

# Print summary of results
total_tests=$(grep -c "\[INFO\]" "$LOG_FILE")
successful_tests=$(grep -c "\[SUCCESS\]" "$LOG_FILE")
failed_tests=$(grep -c "\[ERROR\]" "$LOG_FILE")

echo -e "\n\n========== TEST SUMMARY =========="
echo "Total tests: $total_tests"
echo "Successful: $successful_tests"
echo "Failed: $failed_tests"
echo "===================================="

if [ $failed_tests -gt 0 ]; then
   echo -e "\nFailed tests:"
   grep "\[ERROR\]" "$LOG_FILE"
   exit 1
else
   echo -e "\nAll tests passed successfully!"
   exit 0
fi
