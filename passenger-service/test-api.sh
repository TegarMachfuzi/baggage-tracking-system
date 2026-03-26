#!/bin/bash

BASE_URL="https://passenger-service-tegar20-dev.apps.rm3.7wse.p1.openshiftapps.com"

echo "=== Passenger Service API Tests ==="
echo ""

# Health Check
echo "1. Health Check"
curl -s "$BASE_URL/actuator/health" | jq .
echo ""
echo ""

# Create Passenger
echo "2. Create Passenger"
curl -s -X POST "$BASE_URL/api/passengers" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "+628123456789",
    "passportNumber": "A12345678",
    "nationality": "Indonesia"
  }' | jq .
echo ""
echo ""

# Get All Passengers
echo "3. Get All Passengers"
curl -s "$BASE_URL/api/passengers" | jq .
echo ""
echo ""

# Get Passenger by ID (replace with actual ID from create response)
echo "4. Get Passenger by ID"
PASSENGER_ID="<replace-with-id>"
curl -s "$BASE_URL/api/passengers/$PASSENGER_ID" | jq .
echo ""
echo ""

# Update Passenger
echo "5. Update Passenger"
curl -s -X PUT "$BASE_URL/api/passengers/$PASSENGER_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe Updated",
    "email": "john.doe@example.com",
    "phone": "+628123456789",
    "passportNumber": "A12345678",
    "nationality": "Indonesia"
  }' | jq .
echo ""
echo ""

# Delete Passenger
echo "6. Delete Passenger"
curl -s -X DELETE "$BASE_URL/api/passengers/$PASSENGER_ID" | jq .
echo ""
echo ""

echo "=== Tests Complete ==="
