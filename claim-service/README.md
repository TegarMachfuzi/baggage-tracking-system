# Claim Service

Service untuk mengelola klaim bagasi (lost, damaged, delayed).

## Features

- ✅ CRUD claims (7 endpoints)
- ✅ Link to baggage & passenger
- ✅ Status management (PENDING, IN_PROGRESS, RESOLVED, REJECTED)
- ✅ Kafka events (claim-created)
- ✅ Redis caching
- ✅ PostgreSQL persistence

## Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/claim` | Create new claim |
| GET | `/api/claim/{id}` | Get claim by ID |
| GET | `/api/claim/baggage/{baggageId}` | Get claims by baggage |
| GET | `/api/claim/passenger/{passengerId}` | Get claims by passenger |
| GET | `/api/claim` | Get all claims |
| PUT | `/api/claim/{id}/status` | Update claim status |
| DELETE | `/api/claim/{id}` | Delete claim |

## Configuration

```properties
server.port=8084

spring.datasource.url=jdbc:postgresql://localhost:5432/claim_db
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.kafka.bootstrap-servers=localhost:9092
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

## Run Locally

```bash
# Build lib-common first
cd ../lib-common
mvn clean install

# Build and run
cd ../claim-service
mvn clean package
mvn spring-boot:run
```

## Test

### 1. Create Claim

```bash
curl -X POST http://localhost:8084/api/claim \
  -H "Content-Type: application/json" \
  -d '{
    "baggageId": "550e8400-e29b-41d4-a716-446655440000",
    "passengerId": "550e8400-e29b-41d4-a716-446655440001",
    "claimType": "LOST",
    "description": "Baggage not arrived at destination"
  }'
```

### 2. Get Claim by ID

```bash
curl http://localhost:8084/api/claim/{claimId}
```

### 3. Get Claims by Baggage

```bash
curl http://localhost:8084/api/claim/baggage/{baggageId}
```

### 4. Update Status

```bash
curl -X PUT "http://localhost:8084/api/claim/{claimId}/status?status=IN_PROGRESS"
```

## Claim Types

- `LOST` - Baggage lost
- `DAMAGED` - Baggage damaged
- `DELAYED` - Baggage delayed

## Claim Status

- `PENDING` - Claim submitted, awaiting review
- `IN_PROGRESS` - Claim being investigated
- `RESOLVED` - Claim resolved
- `REJECTED` - Claim rejected

## Kafka Events

### claim-created

Published when new claim is created.

```json
{
  "claimId": "uuid",
  "baggageId": "uuid",
  "passengerId": "uuid",
  "claimType": "LOST",
  "status": "PENDING",
  "description": "...",
  "timestamp": "2026-03-03T10:00:00"
}
```

## Redis Caching

- Cache key: `claim:{id}`
- TTL: 1 hour
- Invalidated on update/delete

## Database Schema

```sql
CREATE TABLE claim (
    id UUID PRIMARY KEY,
    baggage_id UUID NOT NULL,
    passenger_id UUID NOT NULL,
    claim_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    resolved_at TIMESTAMP
);
```

## Dependencies

- Spring Boot 3.5.0
- Spring Data JPA
- PostgreSQL
- Spring Kafka
- Spring Data Redis
- lib-common

## Status

**COMPLETE** - Ready for testing

---

**Last Updated**: 2026-03-03
