# Baggage Tracking System - Data Flow Architecture

## 🏗️ System Architecture Overview

```
┌─────────────┐
│   Client    │
│ (Mobile/Web)│
└──────┬──────┘
       │
       ↓
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway                             │
│              (Routing, Auth, Rate Limiting)                  │
└──────┬──────────────────────────────────────────────────────┘
       │
       ├──→ baggage-service
       ├──→ passenger-service
       ├──→ tracking-service
       ├──→ claim-service
       └──→ notification-service
       
       ↓ (All services use lib-common)
       
┌──────────────────────────────────────────────────────────────┐
│                        lib-common                             │
│  (Entities, DTOs, Mappers, Utils, Kafka Config, Redis)      │
└──────────────────────────────────────────────────────────────┘
       │
       ├──→ PostgreSQL (Persistent Storage)
       ├──→ Kafka (Event Streaming)
       └──→ Redis (Caching)
```

---

## 📊 Detailed Data Flow Scenarios

### Scenario 1: Check-in Baggage (Create Baggage)

```
┌─────────┐
│ Client  │
└────┬────┘
     │ POST /api/baggage
     │ {
     │   "passengerId": "uuid",
     │   "flightNumber": "GA123",
     │   "origin": "CGK",
     │   "destination": "DPS"
     │ }
     ↓
┌────────────────────┐
│  API Gateway       │
└────────┬───────────┘
         │
         ↓
┌────────────────────────────────────────────────────────────┐
│              baggage-service                                │
│                                                             │
│  1. Controller receives BaggageReqDto                      │
│     ↓                                                       │
│  2. Service Layer:                                         │
│     • Generate barcode: BarcodeGenerator.generate()        │
│       → "BAG20260224A1B2C3"                                │
│     • Validate passengerId exists (call passenger-service) │
│     • Set status = BaggageStatus.CHECKED_IN                │
│     • Map DTO → Entity: BaggageMapper.toEntity()           │
│     ↓                                                       │
│  3. Repository.save(entity) → PostgreSQL                   │
│     ↓                                                       │
│  4. Map Entity → DTO: BaggageMapper.toDto()                │
│     ↓                                                       │
│  5. Publish Event to Kafka:                                │
│     • Topic: "baggage-created"                             │
│     • Payload: BaggageEventDto {                           │
│         baggageId, barcode, passengerId,                   │
│         status, timestamp, eventType: "CREATED"            │
│       }                                                     │
│     ↓                                                       │
│  6. Return: ResponseUtil.success(baggageResDto)            │
└────────────────────┬───────────────────────────────────────┘
                     │
                     ↓
              ┌─────────────┐
              │  PostgreSQL │
              │   baggage   │
              │    table    │
              └─────────────┘
                     │
                     ↓
              ┌─────────────┐
              │    Kafka    │
              │   Topic:    │
              │ baggage-    │
              │  created    │
              └──────┬──────┘
                     │
                     ↓
┌────────────────────────────────────────────────────────────┐
│         kafka-consumer-service                              │
│                                                             │
│  1. Consume BaggageEventDto                                │
│     ↓                                                       │
│  2. Store in Redis cache:                                  │
│     • Key: "baggage:{baggageId}"                           │
│     • Value: BaggageEventDto (JSON)                        │
│     • TTL: 24 hours                                        │
│     ↓                                                       │
│  3. Create NotificationEventDto:                           │
│     • Get passenger info from passenger-service            │
│     • Message: "Baggage BAG20260224A1B2C3 checked in"      │
│     ↓                                                       │
│  4. Publish to Kafka:                                      │
│     • Topic: "notification"                                │
│     • Payload: NotificationEventDto                        │
└────────────────────┬───────────────────────────────────────┘
                     │
                     ↓
              ┌─────────────┐
              │    Kafka    │
              │   Topic:    │
              │notification │
              └──────┬──────┘
                     │
                     ↓
┌────────────────────────────────────────────────────────────┐
│         notification-service                                │
│                                                             │
│  1. Consume NotificationEventDto                           │
│     ↓                                                       │
│  2. Send notification based on type:                       │
│     • EMAIL → Send via SMTP                                │
│     • SMS → Send via SMS Gateway                           │
│     • PUSH → Send via FCM/APNS                             │
│     ↓                                                       │
│  3. Log notification status                                │
└────────────────────────────────────────────────────────────┘
                     │
                     ↓
              ┌─────────────┐
              │  Passenger  │
              │  receives   │
              │notification │
              └─────────────┘
```

---

### Scenario 2: Track Baggage Movement

```
┌─────────┐
│ Airport │
│ Scanner │
└────┬────┘
     │ POST /api/tracking
     │ {
     │   "baggageId": "uuid",
     │   "location": "Gate 5 - Loading Area",
     │   "status": "LOADING_TO_AIRCRAFT",
     │   "remarks": "Loaded to flight GA123"
     │ }
     ↓
┌────────────────────────────────────────────────────────────┐
│              tracking-service                               │
│                                                             │
│  1. Controller receives TrackingReqDto                     │
│     ↓                                                       │
│  2. Service Layer:                                         │
│     • Validate baggageId exists                            │
│     • Create TrackingEntity                                │
│     • Set timestamp = LocalDateTime.now()                  │
│     ↓                                                       │
│  3. Repository.save(entity) → PostgreSQL                   │
│     ↓                                                       │
│  4. Update BaggageEntity.status:                           │
│     • Call baggage-service API                             │
│     • Update status to "LOADING_TO_AIRCRAFT"               │
│     ↓                                                       │
│  5. Publish Event to Kafka:                                │
│     • Topic: "tracking-updated"                            │
│     • Payload: TrackingEventDto                            │
│     ↓                                                       │
│  6. Return: ResponseUtil.success(trackingResDto)           │
└────────────────────┬───────────────────────────────────────┘
                     │
                     ↓
              ┌─────────────┐
              │  PostgreSQL │
              │  tracking   │
              │    table    │
              └─────────────┘
                     │
                     ↓
              ┌─────────────┐
              │    Kafka    │
              │   Topic:    │
              │ tracking-   │
              │  updated    │
              └──────┬──────┘
                     │
                     ↓
┌────────────────────────────────────────────────────────────┐
│         kafka-consumer-service                              │
│                                                             │
│  1. Consume TrackingEventDto                               │
│     ↓                                                       │
│  2. Update Redis cache:                                    │
│     • Key: "tracking:{baggageId}:latest"                   │
│     • Value: TrackingEventDto (JSON)                       │
│     • Key: "tracking:{baggageId}:history"                  │
│     • Value: List<TrackingEventDto> (append)               │
│     ↓                                                       │
│  3. Check if notification needed:                          │
│     • If status = ARRIVED → Notify passenger               │
│     • If status = DELAYED → Notify passenger               │
│     • If status = MISSING → Notify passenger & staff       │
│     ↓                                                       │
│  4. Publish NotificationEventDto to Kafka                  │
└────────────────────────────────────────────────────────────┘
```

---

### Scenario 3: Create Claim (Lost/Damaged Baggage)

```
┌─────────┐
│ Client  │
└────┬────┘
     │ POST /api/claim
     │ {
     │   "baggageId": "uuid",
     │   "passengerId": "uuid",
     │   "claimType": "LOST",
     │   "description": "Baggage not arrived after 24 hours"
     │ }
     ↓
┌────────────────────────────────────────────────────────────┐
│              claim-service                                  │
│                                                             │
│  1. Controller receives ClaimReqDto                        │
│     ↓                                                       │
│  2. Service Layer:                                         │
│     • Validate baggageId & passengerId                     │
│     • Validate description not empty                       │
│     • Create ClaimEntity                                   │
│     • Set status = ClaimStatus.SUBMITTED                   │
│     • Set createdAt = LocalDateTime.now()                  │
│     ↓                                                       │
│  3. Repository.save(entity) → PostgreSQL                   │
│     ↓                                                       │
│  4. Update BaggageEntity.status:                           │
│     • Call baggage-service API                             │
│     • Update status to "LOST" or "DAMAGED"                 │
│     ↓                                                       │
│  5. Publish Event to Kafka:                                │
│     • Topic: "claim-created"                               │
│     • Payload: ClaimEventDto                               │
│     ↓                                                       │
│  6. Return: ResponseUtil.success(claimResDto)              │
└────────────────────┬───────────────────────────────────────┘
                     │
                     ↓
              ┌─────────────┐
              │  PostgreSQL │
              │    claim    │
              │    table    │
              └─────────────┘
                     │
                     ↓
              ┌─────────────┐
              │    Kafka    │
              │   Topic:    │
              │   claim-    │
              │  created    │
              └──────┬──────┘
                     │
                     ↓
┌────────────────────────────────────────────────────────────┐
│         kafka-consumer-service                              │
│                                                             │
│  1. Consume ClaimEventDto                                  │
│     ↓                                                       │
│  2. Create multiple notifications:                         │
│     • To Passenger: "Your claim has been submitted"        │
│     • To Staff: "New claim requires investigation"         │
│     ↓                                                       │
│  3. Publish 2x NotificationEventDto to Kafka               │
└────────────────────────────────────────────────────────────┘
```

---

### Scenario 4: Real-time Tracking Query (Using Redis Cache)

```
┌─────────┐
│ Client  │
└────┬────┘
     │ GET /api/tracking/baggage/{baggageId}
     ↓
┌────────────────────────────────────────────────────────────┐
│              tracking-service                               │
│                                                             │
│  1. Controller receives request                            │
│     ↓                                                       │
│  2. Service Layer:                                         │
│     • Check Redis cache first:                             │
│       Key: "tracking:{baggageId}:latest"                   │
│     ↓                                                       │
│  3. If cache HIT:                                          │
│     • Return cached TrackingEventDto                       │
│     • Response time: ~5ms                                  │
│     ↓                                                       │
│  4. If cache MISS:                                         │
│     • Query PostgreSQL                                     │
│     • Get latest tracking record                           │
│     • Store in Redis (TTL: 1 hour)                         │
│     • Return TrackingResDto                                │
│     • Response time: ~50ms                                 │
│     ↓                                                       │
│  5. Return: ResponseUtil.success(data)                     │
└────────────────────────────────────────────────────────────┘
```

---

## 🔄 Event-Driven Architecture Flow

```
┌──────────────────┐
│  Service Layer   │
│  (Any Service)   │
└────────┬─────────┘
         │
         │ 1. Business Logic Executed
         │ 2. Data Saved to PostgreSQL
         │
         ↓
┌────────────────────────────────────────┐
│  KafkaTemplate.send(topic, eventDto)   │
└────────┬───────────────────────────────┘
         │
         ↓
┌────────────────────────────────────────┐
│         Apache Kafka Broker            │
│                                        │
│  Topics:                               │
│  • baggage-created                     │
│  • baggage-updated                     │
│  • tracking-updated                    │
│  • claim-created                       │
│  • notification                        │
└────────┬───────────────────────────────┘
         │
         ├──→ kafka-consumer-service (Consumer Group 1)
         │    • Update Redis cache
         │    • Trigger notifications
         │
         └──→ notification-service (Consumer Group 2)
              • Send emails
              • Send SMS
              • Send push notifications
```

---

## 🗂️ Redis Cache Strategy

### Cache Keys Structure:
```
baggage:{baggageId}                    → Full baggage info (TTL: 24h)
tracking:{baggageId}:latest            → Latest tracking status (TTL: 1h)
tracking:{baggageId}:history           → List of all tracking events (TTL: 24h)
passenger:{passengerId}                → Passenger info (TTL: 24h)
claim:{claimId}                        → Claim details (TTL: 48h)
```

### Cache Update Flow:
```
1. Write to PostgreSQL (Source of Truth)
2. Publish event to Kafka
3. Consumer updates Redis cache
4. Subsequent reads from Redis (fast)
5. Cache invalidation on updates
```

---

## 📝 API Response Examples

### Success Response:
```json
{
  "responseCode": "00",
  "responseMessage": "SUCCESS",
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "barcode": "BAG20260224A1B2C3",
    "passengerId": "987fcdeb-51a2-43f1-b456-426614174111",
    "flightNumber": "GA123",
    "origin": "CGK",
    "destination": "DPS",
    "status": "CHECKED_IN",
    "lastUpdated": "2026-02-24T10:15:30"
  }
}
```

### Error Response:
```json
{
  "responseCode": "96",
  "responseMessage": "FAILED",
  "data": [
    "Passenger ID not found",
    "Flight number is required"
  ]
}
```

---

## 🚀 Performance Considerations

| Operation | Without Cache | With Redis Cache | Improvement |
|-----------|---------------|------------------|-------------|
| Get Baggage Status | ~50ms | ~5ms | 10x faster |
| Get Tracking History | ~100ms | ~8ms | 12x faster |
| Real-time Updates | Polling (slow) | Event-driven (instant) | Real-time |

---

## 🔐 Security Flow (Future - API Gateway)

```
Client Request
    ↓
API Gateway
    ├─→ 1. Authentication (JWT validation)
    ├─→ 2. Authorization (Role check)
    ├─→ 3. Rate Limiting
    └─→ 4. Route to service
         ↓
    Microservice (with lib-common)
```

---

**Ready to implement baggage-service!** 🎯
