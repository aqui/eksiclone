#!/bin/bash

# EksiClone API Test Script
# Bu script, EksiClone projesinin API'lerini test eder ve sonuçları loglar

LOG_FILE="eksiclone_api_test_$(date +%Y%m%d_%H%M%S).log"
echo "EksiClone API Test Logs - $(date)" > $LOG_FILE

# Renk kodları
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test sonuçları sayaçları
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Loglama fonksiyonu - hem terminale hem de log dosyasına yazar
log() {
  echo -e "$1"
  echo "$1" | sed 's/\x1b\[[0-9;]*m//g' >> $LOG_FILE
}

# API test fonksiyonu
test_api() {
  local method=$1
  local endpoint=$2
  local payload=$3
  local auth_token=$4
  local description=$5
  local expected_status=$6
  
  TOTAL_TESTS=$((TOTAL_TESTS + 1))
  
  log "\n[TEST $TOTAL_TESTS] $description"
  log "Method: $method"
  log "Endpoint: $endpoint"
  
  if [ ! -z "$payload" ]; then
    log "Payload: $payload"
  fi
  
  AUTH_HEADER=""
  if [ ! -z "$auth_token" ]; then
    AUTH_HEADER="-H \"Authorization: Bearer $auth_token\""
  fi
  
  CURL_CMD="curl -s -o response.json -w \"%{http_code}\" -X $method"
  
  if [ ! -z "$payload" ]; then
    CURL_CMD="$CURL_CMD -H \"Content-Type: application/json\" -d '$payload'"
  fi
  
  if [ ! -z "$auth_token" ]; then
    CURL_CMD="$CURL_CMD -H \"Authorization: Bearer $auth_token\""
  fi
  
  CURL_CMD="$CURL_CMD $endpoint"
  
  log "Command: $CURL_CMD"
  
  # CURL komutunu çalıştır ve HTTP kodunu al
  HTTP_CODE=$(eval $CURL_CMD)
  
  # Beklenen durum kodunu kontrol et
  if [ "$HTTP_CODE" -eq "$expected_status" ]; then
    log "${GREEN}✓ Success:${NC} Status $HTTP_CODE matches expected $expected_status"
    PASSED_TESTS=$((PASSED_TESTS + 1))
  else
    log "${RED}✗ Error:${NC} Status $HTTP_CODE does not match expected $expected_status"
    FAILED_TESTS=$((FAILED_TESTS + 1))
  fi
  
  # Yanıtı loglama
  RESPONSE=$(cat response.json)
  log "Response: $RESPONSE"
  
  # Yapılan temizlik
  rm -f response.json
}

# API Base URL
GATEWAY_URL="http://localhost:8080"
USER_SERVICE_URL="http://localhost:8082"
ROLE_SERVICE_URL="http://localhost:8083"

log "${YELLOW}=== Starting EksiClone API Tests ===${NC}"
log "Date: $(date)"

# 1. Authentication Tests
log "\n${YELLOW}=== Authentication Tests ===${NC}"

# 1.1 Login - Normal User
LOGIN_PAYLOAD='{
  "username": "normal_user",
  "password": "password123"
}'
test_api "POST" "$GATEWAY_URL/api/v1/auth/login" "$LOGIN_PAYLOAD" "" "Login as normal user" 200

# JWT Token'ı kaydet (başarılı giriş varsayıldı)
if [ -f response.json ]; then
  USER_TOKEN=$(grep -o '"token":"[^"]*' response.json | cut -d'"' -f4)
  
  if [ ! -z "$USER_TOKEN" ]; then
    log "Normal user token: $USER_TOKEN"
  else
    log "${RED}Failed to extract normal user token${NC}"
  fi
fi

# 1.2 Login - Admin User
LOGIN_PAYLOAD='{
  "username": "admin_user",
  "password": "password123"
}'
test_api "POST" "$GATEWAY_URL/api/v1/auth/login" "$LOGIN_PAYLOAD" "" "Login as admin user" 200

# Admin JWT Token'ı kaydet
if [ -f response.json ]; then
  ADMIN_TOKEN=$(grep -o '"token":"[^"]*' response.json | cut -d'"' -f4)
  
  if [ ! -z "$ADMIN_TOKEN" ]; then
    log "Admin user token: $ADMIN_TOKEN"
  else
    log "${RED}Failed to extract admin user token${NC}"
  fi
fi

# 1.3 Failed Login Test
LOGIN_PAYLOAD='{
  "username": "invalid_user",
  "password": "wrong_password"
}'
test_api "POST" "$GATEWAY_URL/api/v1/auth/login" "$LOGIN_PAYLOAD" "" "Login with invalid credentials" 401

# 2. User Service Tests
log "\n${YELLOW}=== User Service Tests ===${NC}"

# 2.1 Get All Users (Admin)
test_api "GET" "$GATEWAY_URL/api/v1/users" "" "$ADMIN_TOKEN" "Get all users (admin)" 200

# 2.2 Get User by ID - Admin gets user 3
test_api "GET" "$GATEWAY_URL/api/v1/users/3" "" "$ADMIN_TOKEN" "Get user by ID (admin)" 200

# 2.3 Create User - Admin creates new user
CREATE_USER_PAYLOAD='{
  "username": "test_user",
  "email": "test@example.com",
  "password": "password123",
  "name": "Test",
  "lastName": "User",
  "roleNames": ["ROLE_USER"]
}'
test_api "POST" "$GATEWAY_URL/api/v1/users" "$CREATE_USER_PAYLOAD" "$ADMIN_TOKEN" "Create new user (admin)" 201

# Yeni oluşturulan kullanıcının ID'sini alma (başarılı oluşturma varsayıldı)
if [ -f response.json ]; then
  NEW_USER_ID=$(grep -o '"id":[0-9]*' response.json | cut -d':' -f2)
  
  if [ ! -z "$NEW_USER_ID" ]; then
    log "New user ID: $NEW_USER_ID"
  else
    log "${RED}Failed to extract new user ID${NC}"
    NEW_USER_ID=4  # Varsayılan değer
  fi
fi

# 2.4 Update User - Admin updates user
UPDATE_USER_PAYLOAD='{
  "username": "test_user_updated",
  "email": "test_updated@example.com",
  "password": "password123",
  "name": "Test Updated",
  "lastName": "User Updated",
  "roleNames": ["ROLE_USER"]
}'
test_api "PUT" "$GATEWAY_URL/api/v1/users/$NEW_USER_ID" "$UPDATE_USER_PAYLOAD" "$ADMIN_TOKEN" "Update user (admin)" 200

# 2.5 Delete User - Admin deletes user
test_api "DELETE" "$GATEWAY_URL/api/v1/users/$NEW_USER_ID" "" "$ADMIN_TOKEN" "Delete user (admin)" 204

# 3. Role Service Tests
log "\n${YELLOW}=== Role Service Tests ===${NC}"

# 3.1 Get All Roles (Admin)
test_api "GET" "$GATEWAY_URL/api/v1/roles" "" "$ADMIN_TOKEN" "Get all roles (admin)" 200

# 3.2 Get Role by ID - Admin gets role 1
test_api "GET" "$GATEWAY_URL/api/v1/roles/1" "" "$ADMIN_TOKEN" "Get role by ID (admin)" 200

# 3.3 Create Role - Admin creates new role
CREATE_ROLE_PAYLOAD='{
  "roleName": "ROLE_TEST"
}'
test_api "POST" "$GATEWAY_URL/api/v1/roles" "$CREATE_ROLE_PAYLOAD" "$ADMIN_TOKEN" "Create new role (admin)" 201

# Yeni oluşturulan rolün ID'sini alma (başarılı oluşturma varsayıldı)
if [ -f response.json ]; then
  NEW_ROLE_ID=$(grep -o '"id":[0-9]*' response.json | cut -d':' -f2)
  
  if [ ! -z "$NEW_ROLE_ID" ]; then
    log "New role ID: $NEW_ROLE_ID"
  else
    log "${RED}Failed to extract new role ID${NC}"
    NEW_ROLE_ID=4  # Varsayılan değer
  fi
fi

# 3.4 Update Role - Admin updates role
UPDATE_ROLE_PAYLOAD='{
  "roleName": "ROLE_TEST_UPDATED"
}'
test_api "PUT" "$GATEWAY_URL/api/v1/roles/$NEW_ROLE_ID" "$UPDATE_ROLE_PAYLOAD" "$ADMIN_TOKEN" "Update role (admin)" 200

# 3.5 Delete Role - Admin deletes role
test_api "DELETE" "$GATEWAY_URL/api/v1/roles/$NEW_ROLE_ID" "" "$ADMIN_TOKEN" "Delete role (admin)" 204

# 4. Authorization Tests
log "\n${YELLOW}=== Authorization Tests ===${NC}"

# 4.1 Regular user tries to access admin-only resource
test_api "GET" "$GATEWAY_URL/api/v1/users" "" "$USER_TOKEN" "Regular user tries to get all users" 403

# 4.2 Unauthenticated user tries to access protected resource
test_api "GET" "$GATEWAY_URL/api/v1/users/1" "" "" "Unauthenticated user tries to get user info" 401

# 5. JWKS Endpoint Test
log "\n${YELLOW}=== JWKS Endpoint Test ===${NC}"
test_api "GET" "$GATEWAY_URL/api/v1/auth/jwks.json" "" "" "Get JWKS for JWT validation" 200

# Özet istatistikler
log "\n${YELLOW}=== Test Summary ===${NC}"
log "Total Tests: $TOTAL_TESTS"
log "${GREEN}Passed Tests: $PASSED_TESTS${NC}"
log "${RED}Failed Tests: $FAILED_TESTS${NC}"
log "Success Rate: $(( (PASSED_TESTS * 100) / TOTAL_TESTS ))%"
log "Log file: $LOG_FILE"

# Çalışan Eureka server ve servisler hakkında bilgi alma
log "\n${YELLOW}=== Running Services ===${NC}"
EUREKA_STATUS=$(curl -s http://localhost:8761/eureka/apps | grep -c "<application>")
log "Eureka Server: $(if [ $EUREKA_STATUS -gt 0 ]; then echo "${GREEN}Running${NC}"; else echo "${RED}Not running${NC}"; fi)"

# User Service durumu
USER_SERVICE_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/actuator/health 2>/dev/null || echo "000")
log "User Service: $(if [ "$USER_SERVICE_STATUS" = "200" ]; then echo "${GREEN}Running${NC}"; else echo "${RED}Not running ($USER_SERVICE_STATUS)${NC}"; fi)"

# Role Service durumu
ROLE_SERVICE_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8083/actuator/health 2>/dev/null || echo "000")
log "Role Service: $(if [ "$ROLE_SERVICE_STATUS" = "200" ]; then echo "${GREEN}Running${NC}"; else echo "${RED}Not running ($ROLE_SERVICE_STATUS)${NC}"; fi)"

# Gateway durumu
GATEWAY_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health 2>/dev/null || echo "000")
log "Gateway: $(if [ "$GATEWAY_STATUS" = "200" ]; then echo "${GREEN}Running${NC}"; else echo "${RED}Not running ($GATEWAY_STATUS)${NC}"; fi)"

log "\n${YELLOW}=== Test Completed ===${NC}"
