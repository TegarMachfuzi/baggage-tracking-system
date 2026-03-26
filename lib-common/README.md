# lib-common Documentation

## Overview
`lib-common` adalah shared library yang berisi komponen-komponen umum yang digunakan oleh semua microservices dalam Baggage Tracking System.

---

## 📦 Components

### 1. **Entities (Model Layer)**
Database entities yang merepresentasikan tabel di PostgreSQL.

| Entity | Table | Description |
|--------|-------|-------------|
| `BaggageEntity` | baggage | Data bagasi penumpang |
| `PassengerEntity` | passenger | Data penumpang |
| `TrackingEntity` | tracking | History tracking bagasi |
| `ClaimEntity` | claim | Klaim bagasi (lost/damaged) |

---

### 2. **DTOs (Data Transfer Objects)**

#### **Request DTOs** (Client → Service)
- `BaggageReqDto` - Create/Update bagasi
- `PassengerReqDto` - Create/Update penumpang
- `TrackingReqDto` - Create tracking checkpoint
- `ClaimReqDto` - Create claim

#### **Response DTOs** (Service → Client)
- `BaggageResDto` - Response bagasi
- `PassengerResDto` - Response penumpang
- `TrackingResDto` - Response tracking
- `ClaimResDto` - Response claim
- `ResponseModel` - Standard wrapper untuk semua response

#### **Event DTOs** (Service → Kafka → Consumer)
- `BaggageEventDto` - Event bagasi created/updated
- `TrackingEventDto` - Event tracking updated
- `ClaimEventDto` - Event claim created
- `NotificationEventDto` - Event untuk trigger notifikasi

---

### 3. **Enums (Constants)**

| Enum | Values | Usage |
|------|--------|-------|
| `BaggageStatus` | CHECKED_IN, IN_TRANSIT, LOADED, IN_FLIGHT, ARRIVED, READY_FOR_PICKUP, DELIVERED, LOST, DELAYED, DAMAGED | Status bagasi |
| `TrackingStatus` | CHECK_IN, SECURITY_SCREENING, LOADING_TO_AIRCRAFT, ON_BOARD, IN_FLIGHT, UNLOADING, BAGGAGE_CLAIM, COLLECTED, TRANSFERRED, DELAYED, MISSING | Status tracking checkpoint |
| `ClaimType` | LOST, DAMAGED, DELAYED, MISSING_ITEMS | Tipe klaim |
| `ClaimStatus` | SUBMITTED, UNDER_INVESTIGATION, APPROVED, REJECTED, RESOLVED, CLOSED | Status klaim |
| `NotificationType` | EMAIL, SMS, PUSH_NOTIFICATION | Tipe notifikasi |

**Response Constants:**
- `RespCodeConstant` - Response codes (00=success, 96=error, 401=unauthorized, 95=notification)
- `RespMsgConstant` - Response messages

---

### 4. **Mappers**
Convert antara Entity dan DTO.

| Mapper | Methods |
|--------|---------|
| `BaggageMapper` | `toEntity(BaggageReqDto)`, `toDto(BaggageEntity)` |
| `PassengerMapper` | `toEntity(PassengerReqDto)`, `toDto(PassengerEntity)` |
| `TrackingMapper` | `toDto(TrackingEntity)`, `toEntity(TrackingResDto)` |
| `ClaimMapper` | `toDto(ClaimEntity)`, `toEntity(ClaimResDto)` |

---

### 5. **Utilities**

| Utility | Purpose | Key Methods |
|---------|---------|-------------|
| `BarcodeGenerator` | Generate unique barcode | `generate()` → BAG20260224XXXXXX |
| `DateUtil` | Date formatting/parsing | `format()`, `parse()`, `formatISO()` |
| `ValidationUtil` | Input validation | `isValidEmail()`, `isValidPhone()`, `isValidUUID()` |
| `ResponseUtil` | Build standard responses | `success(data)`, `error(message)`, `unauthorized()` |

---

### 6. **Configuration**

| Config | Purpose |
|--------|---------|
| `KafkaProducerConfig` | Kafka producer setup dengan JSON serializer |
| `KafkaTopicConfig` | Define 5 Kafka topics |
| `RedisConfig` | Redis connection & template |

**Kafka Topics:**
- `baggage-created` - Bagasi baru dibuat
- `baggage-updated` - Bagasi diupdate
- `tracking-updated` - Tracking checkpoint baru
- `claim-created` - Claim baru dibuat
- `notification` - Trigger notifikasi

---

### 7. **Exception Handling**

| Class | Purpose |
|-------|---------|
| `ValidationException` | Global exception handler untuk validation errors |
| `ErrorException` | Custom error exception |

---

## 🔄 Data Flow Diagram

### **Flow 1: Create Baggage**
```
Client Request (BaggageReqDto)
    ↓
Controller
    ↓
Service Layer
    ├─→ BarcodeGenerator.generate() → Generate barcode
    ├─→ BaggageMapper.toEntity() → Convert DTO to Entity
    ├─→ Repository.save() → Save to PostgreSQL
    ├─→ BaggageMapper.toDto() → Convert Entity to DTO
    ├─→ KafkaTemplate.send("baggage-created", BaggageEventDto) → Publish event
    └─→ ResponseUtil.success(data) → Return response
         ↓
Client Response (ResponseModel<BaggageResDto>)
```

### **Flow 2: Track Baggage**
```
Client Request (TrackingReqDto)
    ↓
Controller
    ↓
Service Layer
    ├─→ ValidationUtil.isValidUUID() → Validate baggageId
    ├─→ TrackingMapper.toEntity() → Convert DTO to Entity
    ├─→ Repository.save() → Save to PostgreSQL
    ├─→ Update BaggageEntity.status → Update bagasi status
    ├─→ KafkaTemplate.send("tracking-updated", TrackingEventDto) → Publish event
    └─→ ResponseUtil.success(data) → Return response
         ↓
Kafka Consumer Service
    ├─→ Consume TrackingEventDto
    ├─→ Update Redis cache
    └─→ Trigger NotificationEventDto → Send to notification topic
         ↓
Notification Service
    └─→ Send email/SMS to passenger
```

### **Flow 3: Create Claim**
```
Client Request (ClaimReqDto)
    ↓
Controller
    ↓
Service Layer
    ├─→ ValidationUtil.isNotEmpty() → Validate description
    ├─→ ClaimMapper.toEntity() → Convert DTO to Entity
    ├─→ Set status = ClaimStatus.SUBMITTED
    ├─→ Repository.save() → Save to PostgreSQL
    ├─→ KafkaTemplate.send("claim-created", ClaimEventDto) → Publish event
    └─→ ResponseUtil.success(data) → Return response
         ↓
Kafka Consumer Service
    └─→ Trigger NotificationEventDto → Notify passenger & staff
```

---

## 📋 Standard Response Format

### Success Response
```json
{
  "responseCode": "00",
  "responseMessage": "SUCCESS",
  "data": {
    "id": "uuid",
    "barcode": "BAG20260224ABC123",
    ...
  }
}
```

### Error Response
```json
{
  "responseCode": "96",
  "responseMessage": "Validation failed",
  "data": ["Field 'email' is required", "Invalid phone number"]
}
```

---

## 🔧 Usage Examples

### 1. Generate Barcode
```java
String barcode = BarcodeGenerator.generate();
// Output: BAG20260224A1B2C3
```

### 2. Build Success Response
```java
BaggageResDto dto = baggageMapper.toDto(entity);
ResponseModel response = ResponseUtil.success(dto);
return ResponseEntity.ok(response);
```

### 3. Validate Input
```java
if (!ValidationUtil.isValidEmail(email)) {
    return ResponseUtil.error("Invalid email format");
}
```

### 4. Publish Kafka Event
```java
BaggageEventDto event = new BaggageEventDto();
event.setBaggageId(baggage.getId());
event.setEventType("CREATED");
event.setTimestamp(LocalDateTime.now());

kafkaTemplate.send(KafkaTopicConfig.BAGGAGE_CREATED_TOPIC, event);
```

### 5. Convert Entity to DTO
```java
BaggageEntity entity = repository.findById(id).orElseThrow();
BaggageResDto dto = BaggageMapper.toDto(entity);
```

---

## 🗄️ Database Schema

### baggage table
```sql
id              UUID PRIMARY KEY
barcode         VARCHAR(50) UNIQUE NOT NULL
passenger_id    UUID NOT NULL
flight_number   VARCHAR(20) NOT NULL
origin          VARCHAR(100) NOT NULL
destination     VARCHAR(100) NOT NULL
status          VARCHAR(50) NOT NULL
last_updated    TIMESTAMP NOT NULL
```

### passenger table
```sql
id              UUID PRIMARY KEY
passenger_name  VARCHAR(255) NOT NULL
passenger_email VARCHAR(255) NOT NULL
passanger_phone VARCHAR(20) NOT NULL
booking_ref     VARCHAR(50) NOT NULL
flight_info     VARCHAR(255) NOT NULL
```

### tracking table
```sql
id              UUID PRIMARY KEY
baggage_id      UUID NOT NULL
location        VARCHAR(255) NOT NULL
status          VARCHAR(50) NOT NULL
timestamp       TIMESTAMP NOT NULL
remarks         TEXT
```

### claim table
```sql
id              UUID PRIMARY KEY
baggage_id      UUID NOT NULL
passenger_id    UUID NOT NULL
claim_type      VARCHAR(50) NOT NULL
status          VARCHAR(50) NOT NULL
description     TEXT NOT NULL
created_at      TIMESTAMP NOT NULL
resolved_at     TIMESTAMP
```

---

## 🚀 Next Steps

Sekarang lib-common sudah lengkap, kita bisa mulai develop microservices dengan urutan:

1. **baggage-service** - CRUD bagasi + publish events
2. **passenger-service** - CRUD penumpang
3. **tracking-service** - Track bagasi + publish events
4. **kafka-consumer-service** - Consume events + update cache
5. **notification-service** - Send notifications
6. **claim-service** - Handle claims
7. **config-server** - Centralized config
8. **api-gateway** - API routing & security

---

## 📝 Notes

- Semua Entity menggunakan UUID sebagai primary key
- Timestamp otomatis di-generate dengan `@PrePersist` / `@PreUpdate`
- Kafka menggunakan JSON serialization
- Redis digunakan untuk caching data tracking real-time
- Response selalu menggunakan `ResponseModel` wrapper
- Validation dilakukan di service layer menggunakan `ValidationUtil`

---

**Version:** 1.0.0  
**Last Updated:** 2026-02-24
