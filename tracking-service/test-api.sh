#!/bin/bash

BASE_URL="http://localhost:8083"

echo "=== Tracking Service API Tests ==="
echo ""

# Health Check
echo "1. Health Check"
curl -s "$BASE_URL/actuator/health" | jq .
echo ""
echo ""

# Create Tracking
echo "2. Create Tracking Checkpoint"
BAGGAGE_ID="123e4567-e89b-12d3-a456-426614174000"
curl -s -X POST "$BASE_URL/api/tracking" \
  -H "Content-Type: application/json" \
  -d "{
    \"baggageId\": \"$BAGGAGE_ID\",
    \"location\": \"Check-in Counter 12\",
    \"status\": \"CHECKED_IN\",
    \"remarks\": \"Baggage checked in\"
  }" | jq .
echo ""
echo ""

sleep 2

# Create Another Tracking
echo "3. Create Another Tracking Checkpoint"
curl -s -X POST "$BASE_URL/api/tracking" \
  -H "Content-Type: application/json" \
  -d "{
    \"baggageId\": \"$BAGGAGE_ID\",
    \"location\": \"Gate 5 - Loading Area\",
    \"status\": \"LOADING_TO_AIRCRAFT\",
    \"remarks\": \"Loaded to flight GA123\"
  }" | jq .
echo ""
echo ""

# Get Tracking History
echo "4. Get Tracking History"
curl -s "$BASE_URL/api/tracking/baggage/$BAGGAGE_ID" | jq .
echo ""
echo ""

# Get Latest Tracking
echo "5. Get Latest Tracking Status"
curl -s "$BASE_URL/api/tracking/latest/$BAGGAGE_ID" | jq .
echo ""
echo ""

echo "=== Tests Complete ==="
