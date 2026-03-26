#!/bin/bash

echo "==================================="
echo "Claim Service API Test"
echo "==================================="

BASE_URL="http://localhost:8084/api/claim"

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo ""
echo -e "${BLUE}1. Create Claim (LOST)${NC}"
CLAIM_RESPONSE=$(curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "baggageId": "550e8400-e29b-41d4-a716-446655440000",
    "passengerId": "550e8400-e29b-41d4-a716-446655440001",
    "claimType": "LOST",
    "description": "Baggage not arrived at destination"
  }')

echo "$CLAIM_RESPONSE" | jq '.'
CLAIM_ID=$(echo "$CLAIM_RESPONSE" | jq -r '.data.id')

echo ""
echo -e "${GREEN}Claim ID: $CLAIM_ID${NC}"

sleep 1

echo ""
echo -e "${BLUE}2. Get Claim by ID${NC}"
curl -s "$BASE_URL/$CLAIM_ID" | jq '.'

sleep 1

echo ""
echo -e "${BLUE}3. Update Claim Status to IN_PROGRESS${NC}"
curl -s -X PUT "$BASE_URL/$CLAIM_ID/status?status=UNDER_INVESTIGATION" | jq '.'

sleep 1

echo ""
echo -e "${BLUE}4. Get All Claims${NC}"
curl -s "$BASE_URL" | jq '.'

echo ""
echo -e "${GREEN}==================================="
echo "Test Complete!"
echo "===================================${NC}"
