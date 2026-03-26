# Baggage Service - Logging System

## Features

✅ **AOP-based Logging** - Automatic logging for all controller and service methods
✅ **Console Logging** - Colored output for development
✅ **File Logging** - Rotating log files (10MB max, 30 days retention)
✅ **Error Logging** - Separate error log file
✅ **SQL Logging** - Debug SQL queries
✅ **Performance Tracking** - Method execution time

---

## Log Levels

- **INFO** - Application logs (default)
- **DEBUG** - SQL queries
- **ERROR** - Errors and exceptions
- **TRACE** - Detailed SQL parameter binding

---

## Log Files

```
logs/
├── baggage-service.log              # All logs
├── baggage-service-error.log        # Errors only
├── baggage-service-2026-02-24.1.log # Archived logs
└── baggage-service-2026-02-24.2.log
```

---

## Log Format

### Console (Colored)
```
2026-02-24 11:05:30.123 INFO  [main] com.baggage.controller.BaggageController - → BaggageController.create() - Args: [BaggageReqDto]
2026-02-24 11:05:30.456 INFO  [main] com.baggage.service.BaggageServiceImpl - → BaggageServiceImpl.create() - Args: [BaggageReqDto]
2026-02-24 11:05:30.789 INFO  [main] com.baggage.service.BaggageServiceImpl - ← BaggageServiceImpl.create() - Success - Duration: 333ms
2026-02-24 11:05:30.890 INFO  [main] com.baggage.controller.BaggageController - ← BaggageController.create() - Success - Duration: 767ms
```

### File
```
2026-02-24 11:05:30.123 INFO  [http-nio-8081-exec-1] com.baggage.controller.BaggageController - → BaggageController.create() - Args: [BaggageReqDto]
```

---

## Example Logs

### Successful Request
```
2026-02-24 11:05:30.123 INFO  → BaggageController.create() - Args: [BaggageReqDto@123]
2026-02-24 11:05:30.456 INFO  → BaggageServiceImpl.create() - Args: [BaggageReqDto@123]
2026-02-24 11:05:30.789 INFO  ← BaggageServiceImpl.create() - Success - Duration: 333ms
2026-02-24 11:05:30.890 INFO  ← BaggageController.create() - Success - Duration: 767ms
```

### Error Request
```
2026-02-24 11:05:30.123 INFO  → BaggageController.getById() - Args: [invalid-uuid]
2026-02-24 11:05:30.456 ERROR ✗ BaggageServiceImpl.getById() - Error: Baggage not found - Duration: 333ms
2026-02-24 11:05:30.567 ERROR Exception in BaggageServiceImpl.getById() - Message: Baggage not found
```

### SQL Query
```
2026-02-24 11:05:30.456 DEBUG Hibernate: insert into baggage (barcode, destination, flight_number, last_updated, origin, passenger_id, status, id) values (?, ?, ?, ?, ?, ?, ?, ?)
2026-02-24 11:05:30.457 TRACE binding parameter [1] as [VARCHAR] - [BAG20260224ABC123]
2026-02-24 11:05:30.458 TRACE binding parameter [2] as [VARCHAR] - [DPS]
```

---

## Configuration

### Change Log Level
Edit `application.properties`:
```properties
# Application logs
logging.level.com.baggage=INFO

# SQL logs
logging.level.org.hibernate.SQL=DEBUG

# Kafka logs
logging.level.org.apache.kafka=WARN
```

### Change Log File Location
Edit `logback-spring.xml`:
```xml
<file>logs/baggage-service.log</file>
```

### Change File Size Limit
Edit `logback-spring.xml`:
```xml
<maxFileSize>10MB</maxFileSize>
```

### Change Retention Days
Edit `logback-spring.xml`:
```xml
<maxHistory>30</maxHistory>
```

---

## Monitoring Logs

### Tail logs in real-time
```bash
tail -f logs/baggage-service.log
```

### Watch errors only
```bash
tail -f logs/baggage-service-error.log
```

### Search logs
```bash
grep "ERROR" logs/baggage-service.log
grep "BaggageController" logs/baggage-service.log
```

### Count errors
```bash
grep -c "ERROR" logs/baggage-service.log
```

---

## Custom Logging in Code

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BaggageServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(BaggageServiceImpl.class);
    
    public void someMethod() {
        log.info("Processing baggage...");
        log.debug("Baggage details: {}", baggage);
        log.warn("Cache miss for baggage: {}", id);
        log.error("Failed to process baggage: {}", e.getMessage());
    }
}
```

---

## Performance Metrics

The logging aspect automatically tracks:
- Method execution time
- Success/failure status
- Input parameters
- Exception details

Example:
```
← BaggageServiceImpl.create() - Success - Duration: 333ms
```

---

## Best Practices

1. ✅ Use INFO for business logic events
2. ✅ Use DEBUG for detailed debugging
3. ✅ Use ERROR for exceptions
4. ✅ Use WARN for potential issues
5. ✅ Don't log sensitive data (passwords, tokens)
6. ✅ Use parameterized logging: `log.info("User: {}", username)`
7. ✅ Monitor error logs regularly

---

## Troubleshooting

### Logs not appearing?
Check log level in `application.properties`

### Log files too large?
Reduce `maxFileSize` in `logback-spring.xml`

### Need more history?
Increase `maxHistory` in `logback-spring.xml`

### SQL queries not showing?
Set `logging.level.org.hibernate.SQL=DEBUG`

---

**Logging system is ready! 📊**
