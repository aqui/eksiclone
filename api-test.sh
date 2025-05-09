#!/bin/bash

# Base URL for the API via gateway
BASE_URL="http://localhost:8080/api/v1/users"

# Function to execute curl and display response
run_curl() {
    local test_name="$1"
    local method="$2"
    local url="$3"
    local data="$4"
    local output_file=$(mktemp)

    echo "Running: $test_name"

    # Execute curl, capture response body and HTTP status
    http_status=$(curl -X "$method" "$url" \
         -H "Content-Type: application/json" \
         -d "$data" \
         -s -o "$output_file" -w "%{http_code}")

    # Check if response body is empty (e.g., for DELETE 204)
    if [ -s "$output_file" ]; then
        cat "$output_file" | jq .
    else
        echo "{}"
    fi
    echo "HTTP Status: $http_status"
    echo

    # Clean up
    rm "$output_file"
}

# Test 1: GET all users
run_curl "GET all users" "GET" "$BASE_URL" ""

# Test 2: GET user by ID (admin_user, ID=1)
run_curl "GET user by ID" "GET" "$BASE_URL/1" ""

# Test 3: POST create a new user
run_curl "POST create new user" "POST" "$BASE_URL" '{
    "username": "test_user2",
    "email": "test2@example.com",
    "password": "$2a$10$XURPShQ5u3QRU1NR3tHgzeM2sOYc1jDB8Za3u4.sO91PiuM98bW1C",
    "name": "Test2",
    "lastName": "User",
    "roleNames": ["ROLE_USER"]
}'

# Test 4: PUT update an existing user (update updated_user, ID=3)
run_curl "PUT update user" "PUT" "$BASE_URL/3" '{
    "username": "updated_user",
    "email": "updated@example.com",
    "password": "$2a$10$XURPShQ5u3QRU1NR3tHgzeM2sOYc1jDB8Za3u4.sO91PiuM98bW1C",
    "name": "Updated",
    "lastName": "User",
    "roleNames": ["ROLE_USER", "ROLE_MODERATOR"]
}'

# Test 5: DELETE user (delete test_user, ID=5)
run_curl "DELETE user" "DELETE" "$BASE_URL/5" ""

# Test 6: POST create user with duplicate username
run_curl "POST create duplicate username" "POST" "$BASE_URL" '{
    "username": "admin_user",
    "email": "new@example.com",
    "password": "$2a$10$XURPShQ5u3QRU1NR3tHgzeM2sOYc1jDB8Za3u4.sO91PiuM98bW1C",
    "name": "New",
    "lastName": "User",
    "roleNames": ["ROLE_USER"]
}'

# Test 7: POST create user with invalid role
run_curl "POST create invalid role" "POST" "$BASE_URL" '{
    "username": "invalid_role_user",
    "email": "invalid@example.com",
    "password": "$2a$10$XURPShQ5u3QRU1NR3tHgzeM2sOYc1jDB8Za3u4.sO91PiuM98bW1C",
    "name": "Invalid",
    "lastName": "User",
    "roleNames": ["ROLE_INVALID"]
}'

# Test 8: GET non-existent user (ID=999)
run_curl "GET non-existent user" "GET" "$BASE_URL/999" ""

# Test 9: PUT update non-existent user (ID=999)
run_curl "PUT update non-existent user" "PUT" "$BASE_URL/999" '{
    "username": "non_existent",
    "email": "nonexistent@example.com",
    "password": "$2a$10$XURPShQ5u3QRU1NR3tHgzeM2sOYc1jDB8Za3u4.sO91PiuM98bW1C",
    "name": "Non",
    "lastName": "Existent",
    "roleNames": ["ROLE_USER"]
}'
