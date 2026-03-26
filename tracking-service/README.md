# Tracking Service

REST API service untuk tracking pergerakan bagasi di berbagai checkpoint.

## Features

- ✅ Create tracking checkpoint
- ✅ Get tracking by ID
- ✅ Get tracking history by baggage ID
- ✅ Get latest tracking status
- ✅ Auto-update baggage status
- ✅ Publish Kafka events
- ✅ Redis caching

## Tech Stack

- Spring Boot 3.5.0
- Spring Data JPA
- PostgreSQL
- Apache Kafka
- Redis
- lib-common

## API Endpoints

### 1. Create Tracking Checkpoint
```http
POST /api/tracking
Content-Type: application/json

{
  "baggageId": "uuid",
  "location": "Gate 5 - Loading Area",
  "status": "LOADING_TO_AIRCRAFT",
  "remarks": "Loaded to flight GA123"
}
```

**Response:**
```json
{
  "responseCode": "00",
  "responseMessage": "SUCCESS",
  "data": {
    "id": "uuid",
    "baggageId": "uuid",
    "location": "Gate 5 - Loading Area",
    "status": "LOADING_TO_AIRCRAFT",
    "timestamp": "2026-02-26T11:30:00",
    "remarks": "Loaded to flight GA123"
  }
}
```

### 2. Get Tracking by ID
```http
GET /api/tracking/{id}
```

### 3. Get Tracking History by Baggage ID
```http
GET /api/tracking/baggage/{baggageId}
```

**Response:**
```json
{
  "responseCode": "00",
  "responseMessage": "SUCCESS",
  "data": [
    {
      "id": "uuid",
      "baggageId": "uuid",
      "location": "Gate 5 - Loading Area",
      "status": "LOADING_TO_AIRCRAFT",
      "timestamp": "2026-02-26T11:30:00",
      "remarks": "Loaded to flight GA123"
    },
    {
      "id": "uuid",
      "baggageId": "uuid",
      "location": "Check-in Counter 12",
      "status": "CHECKED_IN",
      "timestamp": "2026-02-26T10:00:00",
      "remarks": "Baggage checked in"
    }
  ]
}
```

### 4. Get Latest Tracking Status
```http
GET /api/tracking/latest/{baggageId}
```

## Kafka Events

### tracking-updated
Published when new tracking checkpoint is created.

```json
{
  "trackingId": "uuid",
  "baggageId": "uuid",
  "location": "Gate 5 - Loading Area",
  "status": "LOADING_TO_AIRCRAFT",
  "timestamp": "2026-02-26T11:30:00",
  "remarks": "Loaded to flight GA123"
}
```

## Redis Caching

### Cache Keys:
- `tracking:latest:{baggageId}` - Latest tracking status (TTL: 24h)
- `tracking:history:{baggageId}` - Full tracking history (TTL: 24h)

### Cache Strategy:
1. Create tracking → Cache latest, invalidate history
2. Get latest → Check cache first, fallback to DB
3. Get history → Check cache first, fallback to DB

## Configuration

Edit `application.properties`:

```properties
server.port=8083

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/tracking_db
spring.datasource.username=postgres
spring.datasource.password=postgres

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

### 2. Run Service
```bash
cd tracking-service
mvn spring-boot:run
```

Service will start on `http://localhost:8083`

## Testing with cURL

### Create Tracking
```bash
curl -X POST http://localhost:8083/api/tracking \
  -H "Content-Type: application/json" \
  -d '{
    "baggageId": "123e4567-e89b-12d3-a456-426614174000",
    "location": "Gate 5 - Loading Area",
    "status": "LOADING_TO_AIRCRAFT",
    "remarks": "Loaded to flight GA123"
  }'
```

### Get Tracking History
```bash
curl http://localhost:8083/api/tracking/baggage/123e4567-e89b-12d3-a456-426614174000
```

### Get Latest Status
```bash
curl http://localhost:8083/api/tracking/latest/123e4567-e89b-12d3-a456-426614174000
```

## Database Schema

```sql
CREATE TABLE tracking (
    id UUID PRIMARY KEY,
    baggage_id UUID NOT NULL,
    location VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    remarks TEXT
);

CREATE INDEX idx_tracking_baggage_id ON tracking(baggage_id);
CREATE INDEX idx_tracking_timestamp ON tracking(timestamp);
```

## Integration

### Auto-Update Baggage Status
When tracking is created, service automatically calls baggage-service to update status:
```
POST /api/tracking → Update baggage status via REST call
```

## Error Handling

All errors return standard ResponseModel:

```json
{
  "responseCode": "96",
  "responseMessage": "Tracking not found",
  "data": null
}
```

---

**Status**: ✅ Complete  
**Port**: 8083  
**Version**: 0.0.1-SNAPSHOT
