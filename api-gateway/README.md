# API Gateway

Single entry point untuk semua microservices menggunakan Spring Cloud Gateway.

## Features

- ✅ Route to all services (baggage, passenger, tracking, claim)
- ✅ CORS configuration
- ✅ Request/Response logging
- ✅ Load balancing ready
- ✅ Actuator endpoints

## Routes

| Path | Target Service | Port |
|------|---------------|------|
| `/api/baggage/**` | baggage-service | 8081 |
| `/api/passenger/**` | passenger-service | 8082 |
| `/api/tracking/**` | tracking-service | 8083 |
| `/api/claim/**` | claim-service | 8084 |

## Configuration

```yaml
server:
  port: 8080

spring:
  cloud:
    gateway:
      routes:
        - id: baggage-service
          uri: http://localhost:8081
          predicates:
            - Path=/api/baggage/**
```

## Run Locally

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run
```

## Test

### Via Gateway (Port 8080)

```bash
# Create baggage via gateway
curl -X POST http://localhost:8080/api/baggage \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": "550e8400-e29b-41d4-a716-446655440000",
    "flightNumber": "GA123",
    "origin": "CGK",
    "destination": "DPS"
  }'

# Get passenger via gateway
curl http://localhost:8080/api/passenger/{id}

# Create tracking via gateway
curl -X POST http://localhost:8080/api/tracking \
  -H "Content-Type: application/json" \
  -d '{
    "baggageId": "uuid",
    "location": "CGK Terminal 3",
    "status": "CHECKED_IN"
  }'

# Create claim via gateway
curl -X POST http://localhost:8080/api/claim \
  -H "Content-Type: application/json" \
  -d '{
    "baggageId": "uuid",
    "passengerId": "uuid",
    "claimType": "LOST",
    "description": "Baggage not arrived"
  }'
```

### Health Check

```bash
# Gateway health
curl http://localhost:8080/actuator/health

# Gateway routes
curl http://localhost:8080/actuator/gateway/routes
```

## Architecture

```
Client (Mobile/Web)
        ↓
   API Gateway (8080)
        ↓
    ┌───┴───┬───────┬────────┐
    ↓       ↓       ↓        ↓
  8081    8082    8083     8084
baggage passenger tracking claim
```

## Features

### 1. Request Logging
All requests are logged:
```
INFO: Incoming request: POST /api/baggage
INFO: Response: POST /api/baggage - Status: 200
```

### 2. CORS
Configured to allow all origins (development):
```yaml
allowedOrigins: "*"
allowedMethods: GET, POST, PUT, DELETE, OPTIONS
```

### 3. Load Balancing
Ready for multiple instances:
```yaml
uri: lb://baggage-service  # With service discovery
```

## TODO (Future Enhancements)

- [ ] JWT Authentication
- [ ] Rate limiting
- [ ] Circuit breaker (Resilience4j)
- [ ] Request/Response transformation
- [ ] API versioning
- [ ] Service discovery (Eureka)

## Monitoring

```bash
# Check routes
curl http://localhost:8080/actuator/gateway/routes | jq '.'

# Check health
curl http://localhost:8080/actuator/health

# Check metrics
curl http://localhost:8080/actuator/metrics
```

## Dependencies

- Spring Cloud Gateway
- Spring Boot Actuator
- Spring Boot 3.5.0
- Spring Cloud 2025.0.0

## Status

**COMPLETE** - Production Ready (Basic)

- ✅ Routing working
- ✅ CORS configured
- ✅ Logging enabled
- ✅ Docker & K8s ready
- ⚠️ No authentication (add JWT for production)

---

**Last Updated**: 2026-03-03
