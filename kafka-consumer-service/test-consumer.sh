#!/bin/bash

echo "==================================="
echo "Kafka Consumer Service - Test Flow"
echo "==================================="

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo ""
echo -e "${BLUE}Prerequisites:${NC}"
echo "1. Kafka running on localhost:9092"
echo "2. Redis running on localhost:6379"
echo "3. baggage-service running on localhost:8081"
echo "4. kafka-consumer-service running on localhost:8086"
echo ""

read -p "Press Enter to start test..."

echo ""
echo -e "${GREEN}Step 1: Create Baggage (will trigger baggage-created event)${NC}"
BAGGAGE_RESPONSE=$(curl -s -X POST http://localhost:8081/api/baggage \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": "550e8400-e29b-41d4-a716-446655440000",
    "flightNumber": "GA123",
    "origin": "CGK",
    "destination": "DPS"
  }')

echo "$BAGGAGE_RESPONSE" | jq '.'
BAGGAGE_ID=$(echo "$BAGGAGE_RESPONSE" | jq -r '.data.id')
BARCODE=$(echo "$BAGGAGE_RESPONSE" | jq -r '.data.barcode')

echo ""
echo -e "${BLUE}Baggage ID: $BAGGAGE_ID${NC}"
echo -e "${BLUE}Barcode: $BARCODE${NC}"

sleep 2

echo ""
echo -e "${GREEN}Step 2: Check Redis Cache (should be cached by consumer)${NC}"
echo "Checking cache key: baggage:$BAGGAGE_ID"
redis-cli GET "baggage:$BAGGAGE_ID"

echo ""
echo "Checking cache key: baggage:barcode:$BARCODE"
redis-cli GET "baggage:barcode:$BARCODE"

sleep 2

echo ""
echo -e "${GREEN}Step 3: Update Baggage Status (will trigger baggage-updated event)${NC}"
curl -s -X PUT "http://localhost:8081/api/baggage/$BAGGAGE_ID/status?status=IN_TRANSIT" | jq '.'

sleep 2

echo ""
echo -e "${GREEN}Step 4: Check Redis Cache Again (should be updated)${NC}"
redis-cli GET "baggage:$BAGGAGE_ID"

sleep 2

echo ""
echo -e "${GREEN}Step 5: Create Tracking (will trigger tracking-updated event)${NC}"
curl -s -X POST http://localhost:8083/api/tracking \
  -H "Content-Type: application/json" \
  -d "{
    \"baggageId\": \"$BAGGAGE_ID\",
    \"location\": \"CGK Terminal 3\",
    \"status\": \"CHECKED_IN\",
    \"remarks\": \"Baggage checked in\"
  }" | jq '.'

sleep 2

echo ""
echo -e "${GREEN}Step 6: Check Tracking Cache${NC}"
echo "Checking cache key: tracking:latest:$BAGGAGE_ID"
redis-cli GET "tracking:latest:$BAGGAGE_ID"

echo ""
echo -e "${BLUE}==================================="
echo "Test Complete!"
echo "===================================${NC}"
echo ""
echo "Check kafka-consumer-service logs for event processing:"
echo "  tail -f logs/kafka-consumer-service.log"
