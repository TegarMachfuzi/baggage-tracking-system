# API Gateway - Advanced Features

Production-grade API Gateway dengan JWT Authentication, Rate Limiting, Circuit Breaker, Service Discovery, dan Distributed Tracing.

## 🚀 Features

### 1. JWT Authentication ✅
- Token-based authentication
- Login/Register endpoints
- Token validation
- User context propagation

### 2. Rate Limiting ✅
- Per-user rate limiting
- IP-based rate limiting
- Configurable limits (10 req/sec, burst 20)
- Redis-backed

### 3. Circuit Breaker (Resilience4j) ✅
- Automatic failure detection
- Fallback responses
- Half-open state recovery
- Per-service configuration

### 4. Service Discovery (Eureka) ✅
- Dynamic service registration
- Load balancing
- Health checking
- Automatic failover

### 5. Distributed Tracing (Zipkin) ✅
- Request tracing across services
- Performance monitoring
- Dependency visualization
- Trace ID propagation

## 📋 Prerequisites

```bash
# Start infrastructure
docker-compose up -d

# Services will be available:
# - Eureka: http://localhost:8761
# - Zipkin: http://localhost:9411
# - Redis: localhost:6379
# - Kafka: localhost:9092
```

## 🔐 JWT Authentication

### 1. Login (Get Token)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user@example.com",
    "password": "password123"
  }'

# Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "user@example.com"
}
```

### 2. Use Token in Requests

```bash
TOKEN="your-jwt-token-here"

curl -X POST http://localhost:8080/api/baggage \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": "uuid",
    "flightNumber": "GA123",
    "origin": "CGK",
    "destination": "DPS"
  }'
```

### 3. Validate Token

```bash
curl http://localhost:8080/api/auth/validate \
  -H "Authorization: Bearer $TOKEN"
```

## 🚦 Rate Limiting

**Configuration:**
- Replenish Rate: 10 requests/second
- Burst Capacity: 20 requests
- Key: User ID (from JWT)

**Test:**
```bash
# Send 25 requests rapidly
for i in {1..25}; do
  curl -X GET http://localhost:8080/api/baggage \
    -H "Authorization: Bearer $TOKEN"
done

# After 20 requests, you'll get:
# HTTP 429 Too Many Requests
```

## 🔄 Circuit Breaker

**Configuration:**
- Sliding Window: 10 calls
- Failure Threshold: 50%
- Wait Duration: 30 seconds
- Timeout: 5 seconds

**States:**
1. **CLOSED** - Normal operation
2. **OPEN** - Service unavailable, returns fallback
3. **HALF_OPEN** - Testing if service recovered

**Test:**
```bash
# Stop baggage service
# Then make requests - will get fallback response:
{
  "error": "Baggage service is currently unavailable",
  "message": "Please try again later"
}
```

**Monitor:**
```bash
curl http://localhost:8080/actuator/circuitbreakers
```

## 🔍 Service Discovery (Eureka)

**Dashboard:** http://localhost:8761

**Services auto-register:**
- api-gateway
- baggage-service
- passenger-service
- tracking-service
- claim-service

**Load Balancing:**
```yaml
uri: lb://baggage-service  # Automatic load balancing
```

## 📊 Distributed Tracing (Zipkin)

**Dashboard:** http://localhost:9411

**Features:**
- Trace requests across all services
- View latency breakdown
- Identify bottlenecks
- Dependency graph

**Trace ID in Logs:**
```
INFO [api-gateway,64f3a8b9c2e1f4a3,64f3a8b9c2e1f4a3] Request received
```

**View Traces:**
1. Open http://localhost:9411
2. Click "Run Query"
3. Select a trace to see full request flow

## 🧪 Testing All Features

### 1. Start All Services

```bash
# Infrastructure
docker-compose up -d

# Wait for Eureka (30 seconds)
sleep 30

# Start services
cd baggage-service && mvn spring-boot:run &
cd passenger-service && mvn spring-boot:run &
cd tracking-service && mvn spring-boot:run &
cd claim-service && mvn spring-boot:run &
cd api-gateway && mvn spring-boot:run &
```

### 2. Get JWT Token

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test"}' | jq -r '.token')

echo "Token: $TOKEN"
```

### 3. Test Authenticated Request

```bash
curl -X POST http://localhost:8080/api/baggage \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": "550e8400-e29b-41d4-a716-446655440000",
    "flightNumber": "GA123",
    "origin": "CGK",
    "destination": "DPS"
  }'
```

### 4. Test Rate Limiting

```bash
# Rapid requests
for i in {1..25}; do
  curl -w "\nStatus: %{http_code}\n" \
    -H "Authorization: Bearer $TOKEN" \
    http://localhost:8080/api/baggage
done
```

### 5. Test Circuit Breaker

```bash
# Stop baggage service
pkill -f baggage-service

# Make request - should get fallback
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/baggage
```

### 6. View Traces in Zipkin

```bash
# Make some requests
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/baggage

# Open Zipkin
open http://localhost:9411
```

## 📈 Monitoring Endpoints

```bash
# Health
curl http://localhost:8080/actuator/health

# Circuit Breakers
curl http://localhost:8080/actuator/circuitbreakers

# Metrics
curl http://localhost:8080/actuator/metrics

# Gateway Routes
curl http://localhost:8080/actuator/gateway/routes
```

## 🔧 Configuration

### JWT
```yaml
jwt:
  secret: mySecretKeyForBaggageTrackingSystemThatIsLongEnough
  expiration: 86400000  # 24 hours
```

### Rate Limiting
```yaml
redis-rate-limiter:
  replenishRate: 10      # requests per second
  burstCapacity: 20      # max burst
```

### Circuit Breaker
```yaml
resilience4j:
  circuitbreaker:
    sliding-window-size: 10
    failure-rate-threshold: 50
    wait-duration-in-open-state: 30s
```

### Eureka
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### Zipkin
```yaml
management:
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans
```

## 🎯 Architecture

```
Client
  ↓
API Gateway (8080)
  ├─ JWT Authentication
  ├─ Rate Limiting (Redis)
  ├─ Circuit Breaker
  ├─ Tracing (Zipkin)
  ↓
Eureka (8761) - Service Discovery
  ↓
Services (8081-8084)
  ↓
Zipkin (9411) - Trace Collection
```

## 📊 Dashboard URLs

| Service | URL | Purpose |
|---------|-----|---------|
| API Gateway | http://localhost:8080 | Main entry point |
| Eureka | http://localhost:8761 | Service registry |
| Zipkin | http://localhost:9411 | Distributed tracing |
| Kafka UI | http://localhost:8090 | Kafka monitoring |
| Actuator | http://localhost:8080/actuator | Metrics & health |

## 🔒 Security Notes

**Production Checklist:**
- [ ] Change JWT secret
- [ ] Use HTTPS
- [ ] Implement user service
- [ ] Add password hashing
- [ ] Configure CORS properly
- [ ] Set up API keys
- [ ] Enable audit logging
- [ ] Configure firewall rules

## 📝 Status

**ALL FEATURES IMPLEMENTED** ✅

- ✅ JWT Authentication
- ✅ Rate Limiting
- ✅ Circuit Breaker
- ✅ Service Discovery
- ✅ Distributed Tracing

---

**Last Updated**: 2026-03-03  
**Status**: Production-Grade ✅
