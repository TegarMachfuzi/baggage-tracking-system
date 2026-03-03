#!/bin/bash

echo "==================================="
echo "User Service API Test"
echo "==================================="

BASE_URL="http://localhost:8087/api/users"

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo ""
echo -e "${BLUE}1. Register New User${NC}"
REGISTER_RESPONSE=$(curl -s -X POST $BASE_URL/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User",
    "phone": "+1234567890"
  }')

echo "$REGISTER_RESPONSE" | jq '.'
TOKEN=$(echo "$REGISTER_RESPONSE" | jq -r '.token')

echo ""
echo -e "${GREEN}Token: $TOKEN${NC}"

sleep 1

echo ""
echo -e "${BLUE}2. Login with Credentials${NC}"
LOGIN_RESPONSE=$(curl -s -X POST $BASE_URL/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }')

echo "$LOGIN_RESPONSE" | jq '.'
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token')

sleep 1

echo ""
echo -e "${BLUE}3. Validate Token${NC}"
curl -s -X POST $BASE_URL/validate \
  -H "Authorization: Bearer $TOKEN" | jq '.'

sleep 1

echo ""
echo -e "${BLUE}4. Get User by Username${NC}"
curl -s "$BASE_URL/username/testuser" | jq '.'

sleep 1

echo ""
echo -e "${BLUE}5. Get All Users${NC}"
curl -s "$BASE_URL" | jq '.'

echo ""
echo -e "${GREEN}==================================="
echo "Test Complete!"
echo "===================================${NC}"
echo ""
echo "JWT Token for use in other services:"
echo "$TOKEN"
