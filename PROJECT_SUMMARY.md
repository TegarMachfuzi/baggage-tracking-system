# Baggage Tracking System - Project Summary

## 📚 Documentation Index

1. **[lib-common/README.md](lib-common/README.md)** - Complete lib-common documentation
2. **[ARCHITECTURE.md](ARCHITECTURE.md)** - Detailed data flow & architecture diagrams
3. **[QUICK_REFERENCE.md](QUICK_REFERENCE.md)** - Developer cheat sheet & code patterns

---

## ✅ Completed Services

### 1. lib-common Module ✅
- ✅ 4 Entity models (Baggage, Passenger, Tracking, Claim)
- ✅ 5 Enums (BaggageStatus, TrackingStatus, ClaimStatus, ClaimType, NotificationType)
- ✅ 8 DTOs (4 Request + 4 Response)
- ✅ 4 Event DTOs for Kafka
- ✅ 4 Mappers (Entity ↔ DTO conversion)
- ✅ 4 Utility classes (Barcode, Date, Validation, Response)
- ✅ 3 Configuration classes (Kafka Producer, Kafka Topics, Redis)
- ✅ 2 Exception handlers
- ✅ Complete documentation

### 2. baggage-service ✅
- ✅ CRUD baggage (9 endpoints)
- ✅ Generate barcode otomatis
- ✅ Redis caching (by ID & barcode)
- ✅ Kafka events (baggage-created, baggage-updated)
- ✅ Exception handling & logging
- ✅ K8s deployment ready
- ✅ Documentation & test script

### 3. passenger-service ✅
- ✅ CRUD passenger (5 endpoints)
- ✅ Duplicate validation (email & passport)
- ✅ Redis caching
- ✅ Kafka events (passenger-events)
- ✅ Custom exceptions
- ✅ K8s deployment ready
- ✅ Documentation & test script

### 4. tracking-service ✅
- ✅ Create tracking checkpoint (4 endpoints)
- ✅ Get tracking history & latest status
- ✅ Redis caching (latest & history)
- ✅ Kafka events (tracking-updated)
- ✅ Auto-update baggage status
- ✅ K8s deployment ready
- ✅ Documentation & test script

### 5. kafka-consumer-service ✅
- ✅ Consume baggage events (created, updated)
- ✅ Consume tracking events (updated)
- ✅ Consume claim events (created)
- ✅ Auto-update Redis cache
- ✅ Cache invalidation strategy
- ✅ Error handling & logging
- ✅ K8s deployment ready
- ✅ Documentation & test script

**Total: 5 services COMPLETE**

---

## 🏗️ System Architecture

```
Client (Mobile/Web)
    ↓
API Gateway (Port 8080)
    ↓
┌─────────────────────────────────────────────────┐
│              Microservices                      │
├─────────────────────────────────────────────────┤
│ • baggage-service      (Port 8081)              │
│ • passenger-service    (Port 8082)              │
│ • tracking-service     (Port 8083)              │
│ • claim-service        (Port 8084)              │
│ • notification-service (Port 8085)              │
│ • kafka-consumer-service (Port 8086)            │
└─────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────┐
│           Infrastructure (Docker)               │
├─────────────────────────────────────────────────┤
│ • PostgreSQL (Port 5432)                        │
│ • Kafka (Port 9092)                             │
│ • Zookeeper (Port 2181)                         │
│ • Redis (Port 6379)                             │
└─────────────────────────────────────────────────┘
```

---

## 🔄 Data Flow Summary

### 1. Create Baggage Flow
```
Client → API Gateway → baggage-service
    → PostgreSQL (save)
    → Kafka (publish event)
    → kafka-consumer-service (consume)
    → Redis (cache)
    → notification-service (notify passenger)
```

### 2. Track Baggage Flow
```
Scanner → API Gateway → tracking-service
    → PostgreSQL (save tracking)
    → Update baggage status
    → Kafka (publish event)
    → kafka-consumer-service (consume)
    → Redis (update cache)
    → notification-service (notify if needed)
```

### 3. Create Claim Flow
```
Client → API Gateway → claim-service
    → PostgreSQL (save claim)
    → Update baggage status
    → Kafka (publish event)
    → kafka-consumer-service (consume)
    → notification-service (notify passenger & staff)
```

---

## 📊 Database Schema

### Tables:
1. **baggage** - Bagasi data (id, barcode, passenger_id, flight_number, origin, destination, status, last_updated)
2. **passenger** - Penumpang data (id, name, email, phone, booking_ref, flight_info)
3. **tracking** - Tracking history (id, baggage_id, location, status, timestamp, remarks)
4. **claim** - Klaim data (id, baggage_id, passenger_id, claim_type, status, description, created_at, resolved_at)

---

## 🎯 Development Progress

### ✅ Phase 1: Core Services (COMPLETE)
1. **baggage-service** ✅
   - CRUD baggage (9 endpoints)
   - Generate barcode
   - Publish Kafka events
   - Redis caching
   
2. **passenger-service** ✅
   - CRUD passenger (5 endpoints)
   - Duplicate validation
   - Redis caching
   
3. **tracking-service** ✅
   - Create tracking checkpoint (4 endpoints)
   - Get tracking history
   - Real-time status
   - Auto-update baggage status

### ✅ Phase 2: Event Processing (COMPLETE)
4. **kafka-consumer-service** ✅
   - Consume all events (baggage, tracking, claim)
   - Update Redis cache automatically
   - Cache invalidation strategy
   - Error handling & retry logic

### 🚧 Phase 3: Notifications & Claims (NEXT)
5. **notification-service** ← START HERE
   - Email notifications
   - SMS notifications
   - Push notifications (optional)
   - Notification templates
   - Notification history

6. **claim-service**
   - Create claim
   - Update claim status
   - Claim history
   - Link to baggage & passenger

### 📋 Phase 4: Infrastructure
7. **api-gateway**
   - Routing to all services
   - JWT Authentication
   - Rate limiting
   - CORS configuration

8. **config-server** (Optional)
   - Centralized configuration
   - Environment-specific configs
   - Secret management

---

## 🛠️ Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | Spring Boot | 3.5.0 |
| Cloud | Spring Cloud | 2025.0.0 |
| Language | Java | 17 & 23 |
| Database | PostgreSQL | Latest |
| Message Broker | Apache Kafka | 3.6 |
| Cache | Redis | 7.2 |
| Build Tool | Maven | Latest |
| Container | Docker Compose | Latest |

---

## 📝 Key Design Patterns

1. **Microservices Architecture** - Independent, scalable services
2. **Event-Driven Architecture** - Async communication via Kafka
3. **CQRS Pattern** - Separate read (Redis) and write (PostgreSQL)
4. **Repository Pattern** - Data access abstraction
5. **DTO Pattern** - Separate internal/external models
6. **Mapper Pattern** - Entity ↔ DTO conversion
7. **API Gateway Pattern** - Single entry point
8. **Circuit Breaker** - Fault tolerance (future)

---

## 🚀 How to Start Development

### 1. Start Infrastructure
```bash
cd baggage-tracking-system
docker-compose up -d
```

### 2. Build lib-common
```bash
cd lib-common
mvn clean install
```

### 3. Start Developing Services
```bash
cd baggage-service
# Implement controller, service, repository
# Test with Postman
mvn spring-boot:run
```

### 4. Test Flow
```bash
# 1. Create passenger
POST http://localhost:8082/api/passenger

# 2. Create baggage
POST http://localhost:8081/api/baggage

# 3. Track baggage
POST http://localhost:8083/api/tracking

# 4. Get tracking history
GET http://localhost:8083/api/tracking/baggage/{id}
```

---

## 📖 Code Examples

### Standard Controller
```java
@RestController
@RequestMapping("/api/baggage")
public class BaggageController {
    @Autowired
    private BaggageService service;
    
    @PostMapping
    public ResponseEntity<ResponseModel> create(@RequestBody BaggageReqDto request) {
        return ResponseEntity.ok(ResponseUtil.success(service.create(request)));
    }
}
```

### Standard Service
```java
@Service
public class BaggageService {
    @Autowired
    private BaggageRepository repository;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    public BaggageResDto create(BaggageReqDto request) {
        String barcode = BarcodeGenerator.generate();
        BaggageEntity entity = BaggageMapper.toEntity(request);
        entity.setBarcode(barcode);
        entity = repository.save(entity);
        
        // Publish event
        BaggageEventDto event = new BaggageEventDto();
        event.setBaggageId(entity.getId());
        kafkaTemplate.send(KafkaTopicConfig.BAGGAGE_CREATED_TOPIC, event);
        
        return BaggageMapper.toDto(entity);
    }
}
```

---

## 🎓 Learning Resources

- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **Spring Cloud Docs**: https://spring.io/projects/spring-cloud
- **Kafka Docs**: https://kafka.apache.org/documentation/
- **Redis Docs**: https://redis.io/documentation

---

## 📞 Support

For questions or issues:
1. Check documentation files
2. Review QUICK_REFERENCE.md for code patterns
3. Check ARCHITECTURE.md for data flow

---

## 📅 Project Timeline

- **Week 1**: lib-common ✅ + baggage-service ✅ + passenger-service ✅
- **Week 2**: tracking-service ✅ + kafka-consumer-service ✅
- **Week 3**: notification-service + claim-service (in progress)
- **Week 4**: api-gateway + testing + deployment

---

**Status**: Phase 2 COMPLETE ✅ (5 services: lib-common, baggage, passenger, tracking, kafka-consumer)  
**Next**: notification-service implementation  
**Last Updated**: 2026-03-03
