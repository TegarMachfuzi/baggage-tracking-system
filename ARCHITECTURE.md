# Baggage Tracking System - Architecture

## System Overview

```
┌─────────────┐
│   Client    │
│ (Mobile/Web)│
└──────┬──────┘
       │
       ↓
┌──────────────────────────────────────────────────────────────┐
│                        API Gateway :8080                      │
│         (JWT Auth, Rate Limiting, Circuit Breaker)           │
└──────┬───────────────────────────────────────────────────────┘
       │
       ├──→ user-service       :8087  (Auth, JWT)
       ├──→ passenger-service  :8082
       ├──→ baggage-service    :8081
       ├──→ tracking-service   :8083
       └──→ claim-service      :8084

       ↓ (Service Discovery)

┌──────────────────────────────────────────────────────────────┐
│                    Eureka Server :8761                        │
└──────────────────────────────────────────────────────────────┘

       ↓ (Shared Library)

┌──────────────────────────────────────────────────────────────┐
│                        lib-common                             │
│     (Entities, DTOs, Mappers, Utils, Kafka Config)           │
└──────────────────────────────────────────────────────────────┘

       ↓ (Infrastructure)

┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│  PostgreSQL  │   │    Kafka     │   │    Redis     │
│  :5432       │   │  :9092       │   │  :6379       │
│  (Storage)   │   │  (Events)    │   │  (Cache)     │
└──────────────┘   └──────────────┘   └──────────────┘
```

---

## Service Ports

| Service | Port | Description |
|---|---|---|
| api-gateway | 8080 | Single entry point |
| baggage-service | 8081 | Baggage management |
| passenger-service | 8082 | Passenger management |
| tracking-service | 8083 | Baggage tracking |
| claim-service | 8084 | Claim management |
| notification-service | 8085 | Email notifications |
| kafka-consumer-service | 8086 | Kafka event processor |
| user-service | 8087 | Auth & user management |
| eureka-server | 8761 | Service discovery |

---

## Scenario 1: Check-in Baggage

```
Client → POST /api/baggage
         {passengerId, flightNumber, origin, destination}
         ↓
API Gateway (JWT validation)
         ↓
baggage-service
  1. Validate passengerId → call passenger-service GET /api/passengers/{id}
  2. Generate barcode: BAG20260420XXXXX
  3. Save to PostgreSQL (status: CHECKED_IN)
  4. Cache to Redis: key "baggage:{id}"
  5. Publish event → Kafka topic: baggage-created
         ↓
notification-service (consume baggage-created)
  1. Fetch passenger email → call passenger-service
  2. Generate barcode image (ZXing Code128)
  3. Send HTML email with embedded barcode image
```

---

## Scenario 2: Track Baggage Movement

```
Petugas → POST /api/tracking
          {baggageId, location, status, remarks}
          ↓
tracking-service
  1. Save tracking record to PostgreSQL
  2. Update Redis cache (latest & history)
  3. Call baggage-service PATCH /api/baggage/{id}/status → update status
  4. Publish event → Kafka topic: tracking-updated
          ↓
notification-service (consume tracking-updated)
  1. Fetch baggageId → call baggage-service GET /api/baggage/{id}
  2. Fetch passenger email → call passenger-service
  3. Send HTML email with location update + barcode image

Client → GET /api/tracking/barcode/{barcode}   ← track by barcode
      → GET /api/tracking/baggage/{baggageId}  ← track by UUID
      → GET /api/tracking/latest/{baggageId}   ← latest status
      → GET /api/tracking/latest/barcode/{barcode}
```

---

## Scenario 3: Create Claim

```
Client → POST /api/claim
         {baggageId, passengerId, claimType, description}
         ↓
claim-service
  1. Save claim to PostgreSQL (status: SUBMITTED)
  2. Publish event → Kafka topic: claim-created
         ↓
notification-service (consume claim-created)
  1. Fetch passenger email → call passenger-service
  2. Send HTML email: claim confirmation
```

---

## Kafka Topics

| Topic | Producer | Consumer |
|---|---|---|
| `baggage-created` | baggage-service | notification-service, kafka-consumer-service |
| `baggage-updated` | baggage-service | kafka-consumer-service |
| `tracking-updated` | tracking-service | notification-service, kafka-consumer-service |
| `claim-created` | claim-service | notification-service |

---

## Redis Cache Keys

```
baggage:{baggageId}              → BaggageResDto        (TTL: 24h)
baggage:barcode:{barcode}        → BaggageResDto        (TTL: 24h)
tracking:latest:{baggageId}      → TrackingResDto       (TTL: 24h)
tracking:history:{baggageId}     → List<TrackingResDto> (TTL: 24h)
```

---

## Inter-Service Communication

```
baggage-service    → passenger-service  (validate passenger on create)
tracking-service   → baggage-service    (update status on tracking create)
notification-service → passenger-service (get email for notification)
notification-service → baggage-service  (get barcode for tracking notification)
```

---

## Security Flow

```
Client Request
    ↓
API Gateway
  1. JWT validation (JwtAuthenticationFilter)
  2. Public paths bypass: /api/users/register, /api/users/login,
     /api/baggage, /api/passengers, /api/tracking, /api/claim
  3. Rate limiting via Redis (baggage-service route)
  4. Circuit breaker (Resilience4j) per route
    ↓
Microservice
```

---

## Notification Flow (Email)

```
Kafka Event received
    ↓
Fetch passenger email from passenger-service
    ↓
Generate barcode image (ZXing Code128, 400x100px)
    ↓
Build HTML email with inline barcode image (cid:barcode)
    ↓
Send via Gmail SMTP port 465 (SSL)
```

---

## API Response Format

```json
// Success
{
  "responseCode": "00",
  "responseMessage": "SUCCESS",
  "data": { ... }
}

// Error
{
  "responseCode": "96",
  "responseMessage": "Error message",
  "data": null
}
```

---

## Infrastructure (Podman)

```
podman compose up -d

Services: postgres, kafka (KRaft), redis, eureka-server,
          baggage-service, passenger-service, tracking-service,
          claim-service, notification-service, kafka-consumer-service,
          user-service, api-gateway
```
