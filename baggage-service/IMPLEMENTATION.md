# Baggage Service - Implementation Summary

## ✅ Completed Components

### 1. **Controller Layer**
- `BaggageController` - REST API endpoints
  - POST `/api/baggage` - Create baggage
  - GET `/api/baggage/{id}` - Get by ID
  - GET `/api/baggage/barcode/{barcode}` - Get by barcode
  - GET `/api/baggage/passenger/{passengerId}` - Get by passenger
  - GET `/api/baggage/flight/{flightNumber}` - Get by flight
  - GET `/api/baggage` - Get all
  - PUT `/api/baggage/{id}` - Update baggage
  - PATCH `/api/baggage/{id}/status` - Update status only
  - DELETE `/api/baggage/{id}` - Delete baggage

### 2. **Service Layer**
- `BaggageServiceImpl` - Business logic
  - Barcode generation (automatic)
  - Input validation
  - CRUD operations
  - Kafka event publishing
  - Status management

### 3. **Repository Layer**
- `BaggageRepository` - Data access
  - findByBarcode()
  - findByPassengerId()
  - findByStatus()
  - findByFlightNumber()

### 4. **Exception Handling**
- `GlobalExceptionHandler`
  - IllegalArgumentException → 400 Bad Request
  - RuntimeException → 500 Internal Server Error
  - Generic Exception → 500 Internal Server Error

### 5. **Configuration**
- `application.properties`
  - Server port: 8081
  - PostgreSQL connection
  - Kafka producer config
  - Redis config
  - Logging levels

### 6. **Application Entry Point**
- `BaggageServiceApplication` - Spring Boot main class

### 7. **Documentation**
- `README.md` - Complete service documentation

---

## 🔄 Data Flow

```
1. Client sends POST /api/baggage
   ↓
2. BaggageController receives BaggageReqDto
   ↓
3. BaggageServiceImpl:
   • Validates input (ValidationUtil)
   • Generates barcode (BarcodeGenerator)
   • Maps DTO → Entity (BaggageMapper)
   • Sets status = CHECKED_IN
   • Saves to PostgreSQL
   ↓
4. Publishes BaggageEventDto to Kafka
   • Topic: baggage-created
   • Event type: CREATED
   ↓
5. Maps Entity → DTO (BaggageMapper)
   ↓
6. Returns ResponseModel with BaggageResDto
```

---

## 🎯 Key Features

### Automatic Barcode Generation
```java
String barcode = BarcodeGenerator.generate();
// Output: BAG20260224A1B2C3
```

### Input Validation
```java
if (!ValidationUtil.isValidUUID(passengerId)) {
    throw new IllegalArgumentException("Invalid passenger ID");
}
```

### Kafka Event Publishing
```java
// Automatically publishes to:
// - baggage-created (on create)
// - baggage-updated (on update/status change)
```

### Standard Response Format
```json
{
  "responseCode": "00",
  "responseMessage": "SUCCESS",
  "data": { ... }
}
```

---

## 🧪 Testing Guide

### 1. Start Infrastructure
```bash
docker-compose up -d
```

### 2. Build & Install lib-common
```bash
cd lib-common
mvn clean install
```

### 3. Run baggage-service
```bash
cd baggage-service
mvn spring-boot:run
```

### 4. Test with cURL

**Create Baggage:**
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

**Get by Barcode:**
```bash
curl http://localhost:8081/api/baggage/barcode/BAG20260224A1B2C3
```

**Update Status:**
```bash
curl -X PATCH "http://localhost:8081/api/baggage/{id}/status?status=IN_TRANSIT"
```

---

## 📊 Database

Service will auto-create `baggage` table on first run (ddl-auto=update):

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

---

## 🔧 Configuration Checklist

- [x] Server port: 8081
- [x] PostgreSQL connection configured
- [x] Kafka producer configured
- [x] Redis configured
- [x] Logging configured
- [x] lib-common dependency added

---

## 📝 Next Steps

1. ✅ baggage-service complete
2. → Test all endpoints with Postman
3. → Verify Kafka events in Kafka UI/console
4. → Move to **passenger-service** implementation

---

## 🚨 Important Notes

1. **Database**: Make sure PostgreSQL is running and database `baggage_db` exists
2. **Kafka**: Make sure Kafka & Zookeeper are running
3. **lib-common**: Must be installed first (`mvn clean install`)
4. **Port**: Service runs on port 8081 (configurable in application.properties)

---

## 📦 Project Structure

```
baggage-service/
├── src/main/java/com/baggage/
│   ├── BaggageServiceApplication.java
│   ├── controller/
│   │   └── BaggageController.java
│   ├── service/
│   │   └── BaggageServiceImpl.java
│   ├── repository/
│   │   └── BaggageRepository.java
│   └── exception/
│       └── GlobalExceptionHandler.java
├── src/main/resources/
│   └── application.properties
├── pom.xml
└── README.md
```

---

**Status**: ✅ COMPLETE  
**Port**: 8081  
**Dependencies**: lib-common  
**Ready for**: Testing & Integration
