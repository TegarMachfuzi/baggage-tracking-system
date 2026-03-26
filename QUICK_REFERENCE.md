# Quick Reference Guide - lib-common

## 🎯 Cheat Sheet untuk Developer

### 1. Standard Controller Pattern
```java
@RestController
@RequestMapping("/api/baggage")
public class BaggageController {
    
    @Autowired
    private BaggageService service;
    
    @PostMapping
    public ResponseEntity<ResponseModel> create(@RequestBody BaggageReqDto request) {
        BaggageResDto result = service.create(request);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel> getById(@PathVariable UUID id) {
        BaggageResDto result = service.getById(id);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }
}
```

---

### 2. Standard Service Pattern
```java
@Service
public class BaggageService {
    
    @Autowired
    private BaggageRepository repository;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    public BaggageResDto create(BaggageReqDto request) {
        // 1. Validate
        if (!ValidationUtil.isValidUUID(request.getPassengerId().toString())) {
            throw new IllegalArgumentException("Invalid passenger ID");
        }
        
        // 2. Generate barcode
        String barcode = BarcodeGenerator.generate();
        
        // 3. Map & Save
        BaggageEntity entity = BaggageMapper.toEntity(request);
        entity.setBarcode(barcode);
        entity.setStatus(BaggageStatus.CHECKED_IN.name());
        entity = repository.save(entity);
        
        // 4. Publish event
        BaggageEventDto event = new BaggageEventDto();
        event.setBaggageId(entity.getId());
        event.setBarcode(barcode);
        event.setEventType("CREATED");
        event.setTimestamp(LocalDateTime.now());
        kafkaTemplate.send(KafkaTopicConfig.BAGGAGE_CREATED_TOPIC, event);
        
        // 5. Return DTO
        return BaggageMapper.toDto(entity);
    }
}
```

---

### 3. Standard Repository Pattern
```java
@Repository
public interface BaggageRepository extends JpaRepository<BaggageEntity, UUID> {
    Optional<BaggageEntity> findByBarcode(String barcode);
    List<BaggageEntity> findByPassengerId(UUID passengerId);
    List<BaggageEntity> findByStatus(String status);
}
```

---

### 4. Kafka Consumer Pattern
```java
@Service
public class BaggageEventConsumer {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @KafkaListener(topics = KafkaTopicConfig.BAGGAGE_CREATED_TOPIC, groupId = "consumer-group")
    public void consume(BaggageEventDto event) {
        // Update Redis cache
        String key = "baggage:" + event.getBaggageId();
        redisTemplate.opsForValue().set(key, event, 24, TimeUnit.HOURS);
        
        // Trigger notification
        NotificationEventDto notification = new NotificationEventDto();
        notification.setMessage("Baggage " + event.getBarcode() + " checked in");
        notification.setTimestamp(LocalDateTime.now());
        kafkaTemplate.send(KafkaTopicConfig.NOTIFICATION_TOPIC, notification);
    }
}
```

---

### 5. Redis Cache Pattern
```java
@Service
public class TrackingService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private TrackingRepository repository;
    
    public TrackingResDto getLatest(UUID baggageId) {
        // Try cache first
        String cacheKey = "tracking:" + baggageId + ":latest";
        TrackingResDto cached = (TrackingResDto) redisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null) {
            return cached; // Cache HIT
        }
        
        // Cache MISS - query database
        TrackingEntity entity = repository.findTopByBaggageIdOrderByTimestampDesc(baggageId)
            .orElseThrow(() -> new RuntimeException("Tracking not found"));
        
        TrackingResDto dto = TrackingMapper.toDto(entity);
        
        // Store in cache
        redisTemplate.opsForValue().set(cacheKey, dto, 1, TimeUnit.HOURS);
        
        return dto;
    }
}
```

---

### 6. Exception Handling Pattern
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseModel> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
            .body(ResponseUtil.error(ex.getMessage()));
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseModel> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResponseUtil.error(RespMsgConstant.ERROR_SYSTEM));
    }
}
```

---

## 📋 Common Code Snippets

### Generate Barcode
```java
String barcode = BarcodeGenerator.generate();
// Output: BAG20260224A1B2C3
```

### Validate Email
```java
if (!ValidationUtil.isValidEmail(email)) {
    throw new IllegalArgumentException("Invalid email format");
}
```

### Build Success Response
```java
return ResponseEntity.ok(ResponseUtil.success(data));
```

### Build Error Response
```java
return ResponseEntity.badRequest()
    .body(ResponseUtil.error("Validation failed"));
```

### Publish Kafka Event
```java
kafkaTemplate.send(KafkaTopicConfig.BAGGAGE_CREATED_TOPIC, eventDto);
```

### Format Date
```java
String formatted = DateUtil.format(LocalDateTime.now());
// Output: 2026-02-24 10:15:30
```

### Map Entity to DTO
```java
BaggageResDto dto = BaggageMapper.toDto(entity);
```

### Map DTO to Entity
```java
BaggageEntity entity = BaggageMapper.toEntity(requestDto);
```

---

## 🔧 Configuration Checklist

### application.properties (Each Service)
```properties
# Server
server.port=8081

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/baggage_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=baggage-service-group

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.redis.max.total=100
spring.redis.max.idle=100
```

---

## 🎨 Enum Usage Examples

### BaggageStatus
```java
entity.setStatus(BaggageStatus.CHECKED_IN.name());
entity.setStatus(BaggageStatus.IN_TRANSIT.name());
entity.setStatus(BaggageStatus.DELIVERED.name());
```

### TrackingStatus
```java
tracking.setStatus(TrackingStatus.CHECK_IN.name());
tracking.setStatus(TrackingStatus.LOADING_TO_AIRCRAFT.name());
tracking.setStatus(TrackingStatus.ON_BOARD.name());
```

### ClaimType & ClaimStatus
```java
claim.setClaimType(ClaimType.LOST.name());
claim.setStatus(ClaimStatus.SUBMITTED.name());
```

---

## 🚨 Common Pitfalls to Avoid

1. ❌ **Don't forget @Bean annotation**
   ```java
   @Bean  // ← Don't forget this!
   public RedisTemplate<String, Object> redisTemplate() { ... }
   ```

2. ❌ **Don't use Entity in Controller response**
   ```java
   // BAD
   return ResponseEntity.ok(entity);
   
   // GOOD
   return ResponseEntity.ok(ResponseUtil.success(BaggageMapper.toDto(entity)));
   ```

3. ❌ **Don't forget to publish events**
   ```java
   repository.save(entity);
   // Don't forget to publish Kafka event here!
   kafkaTemplate.send(topic, eventDto);
   ```

4. ❌ **Don't skip validation**
   ```java
   // Always validate before processing
   if (ValidationUtil.isEmpty(request.getBarcode())) {
       throw new IllegalArgumentException("Barcode is required");
   }
   ```

5. ❌ **Don't hardcode response codes**
   ```java
   // BAD
   response.setResponseCode("00");
   
   // GOOD
   response.setResponseCode(RespCodeConstant.RC_00);
   ```

---

## 📦 Maven Dependency (For Services)

```xml
<dependency>
    <groupId>com.baggage</groupId>
    <artifactId>lib-common</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

---

## 🧪 Testing Pattern

```java
@SpringBootTest
public class BaggageServiceTest {
    
    @Autowired
    private BaggageService service;
    
    @MockBean
    private BaggageRepository repository;
    
    @Test
    public void testCreateBaggage() {
        BaggageReqDto request = new BaggageReqDto();
        request.setPassengerId(UUID.randomUUID());
        request.setFlightNumber("GA123");
        
        BaggageResDto result = service.create(request);
        
        assertNotNull(result.getId());
        assertNotNull(result.getBarcode());
        assertEquals("CHECKED_IN", result.getStatus());
    }
}
```

---

## 🎯 Next Steps

1. ✅ lib-common complete
2. → Start baggage-service implementation
3. → Use this guide as reference
4. → Copy patterns for other services

---

**Happy Coding! 🚀**
