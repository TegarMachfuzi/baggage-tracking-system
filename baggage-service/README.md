# Baggage Service

REST API service untuk mengelola data bagasi dalam Baggage Tracking System.

## Features

- ✅ Create baggage (check-in)
- ✅ Get baggage by ID
- ✅ Get baggage by barcode
- ✅ Get baggage by passenger ID
- ✅ Get baggage by flight number
- ✅ Update baggage information
- ✅ Update baggage status
- ✅ Delete baggage
- ✅ Publish Kafka events (baggage-created, baggage-updated)

## Tech Stack

- Spring Boot 3.5.0
- Spring Data JPA
- PostgreSQL
- Apache Kafka
- Redis
- lib-common (shared library)

## API Endpoints

### 1. Create Baggage
```http
POST /api/baggage
Content-Type: application/json

{
  "passengerId": "uuid",
  "flightNumber": "GA123",
  "origin": "CGK",
  "destination": "DPS"
}
```

**Response:**
```json
{
  "responseCode": "00",
  "responseMessage": "SUCCESS",
  "data": {
    "id": "uuid",
    "barcode": "BAG20260224A1B2C3",
    "passengerId": "uuid",
    "flightNumber": "GA123",
    "origin": "CGK",
    "destination": "DPS",
    "status": "CHECKED_IN",
    "lastUpdated": "2026-02-24T10:30:00"
  }
}
```

### 2. Get Baggage by ID
```http
GET /api/baggage/{id}
```

### 3. Get Baggage by Barcode
```http
GET /api/baggage/barcode/{barcode}
```

### 4. Get Baggage by Passenger ID
```http
GET /api/baggage/passenger/{passengerId}
```

### 5. Get Baggage by Flight Number
```http
GET /api/baggage/flight/{flightNumber}
```

### 6. Get All Baggage
```http
GET /api/baggage
```

### 7. Update Baggage
```http
PUT /api/baggage/{id}
Content-Type: application/json

{
  "flightNumber": "GA456",
  "origin": "CGK",
  "destination": "SUB",
  "status": "IN_TRANSIT"
}
```

### 8. Update Baggage Status
```http
PATCH /api/baggage/{id}/status?status=IN_TRANSIT
```

### 9. Delete Baggage
```http
DELETE /api/baggage/{id}
```

## Kafka Events

### baggage-created
Published when new baggage is created.

```json
{
  "baggageId": "uuid",
  "barcode": "BAG20260224A1B2C3",
  "passengerId": "uuid",
  "flightNumber": "GA123",
  "origin": "CGK",
  "destination": "DPS",
  "status": "CHECKED_IN",
  "timestamp": "2026-02-24T10:30:00",
  "eventType": "CREATED"
}
```

### baggage-updated
Published when baggage is updated.

```json
{
  "baggageId": "uuid",
  "barcode": "BAG20260224A1B2C3",
  "status": "IN_TRANSIT",
  "timestamp": "2026-02-24T11:00:00",
  "eventType": "UPDATED"
}
```

## Configuration

Edit `application.properties`:

```properties
# Server
server.port=8081

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/baggage_db
spring.datasource.username=postgres
spring.datasource.password=your_password

# Kafka
spring.kafka.bootstrap-servers=localhost:9092

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

## Running the Service

### 1. Start Infrastructure
```bash
docker-compose up -d
```

### 2. Build lib-common
```bash
cd lib-common
mvn clean install
```

### 3. Run Service
```bash
cd baggage-service
mvn spring-boot:run
```

Service will start on `http://localhost:8081`

## Testing with cURL

### Create Baggage
```bash
curl -X POST http://localhost:8081/api/baggage \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": "123e4567-e89b-12d3-a456-426614174000",
    "flightNumber": "GA123",
    "origin": "CGK",
    "destination": "DPS"
  }'
```

### Get Baggage by Barcode
```bash
curl http://localhost:8081/api/baggage/barcode/BAG20260224A1B2C3
```

### Update Status
```bash
curl -X PATCH "http://localhost:8081/api/baggage/{id}/status?status=IN_TRANSIT"
```

## Database Schema

```sql
CREATE TABLE baggage (
    id UUID PRIMARY KEY,
    barcode VARCHAR(50) UNIQUE NOT NULL,
    passenger_id UUID NOT NULL,
    flight_number VARCHAR(20) NOT NULL,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    last_updated TIMESTAMP NOT NULL
);
```

## Dependencies

- lib-common (entities, DTOs, mappers, utils, configs)
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- PostgreSQL Driver
- Spring Kafka
- Spring Data Redis

## Error Handling

All errors return standard ResponseModel:

```json
{
  "responseCode": "96",
  "responseMessage": "Baggage not found",
  "data": null
}
```

## Next Steps

After baggage-service is running:
1. Test all endpoints with Postman
2. Verify Kafka events are published
3. Move to passenger-service implementation

---

**Status**: ✅ Complete  
**Port**: 8081  
**Version**: 0.0.1-SNAPSHOT
