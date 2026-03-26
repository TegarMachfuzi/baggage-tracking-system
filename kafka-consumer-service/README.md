# Kafka Consumer Service

Service untuk consume Kafka events dan update Redis cache.

## Features

- ✅ Consume baggage events (created, updated)
- ✅ Consume tracking events (updated)
- ✅ Consume claim events (created)
- ✅ Auto-update Redis cache
- ✅ Cache invalidation strategy
- ✅ Error handling & logging

## Topics Consumed

| Topic | Event Type | Action |
|-------|-----------|--------|
| `baggage-created` | BaggageEventDto | Cache baggage by ID & barcode |
| `baggage-updated` | BaggageEventDto | Invalidate & re-cache baggage |
| `tracking-updated` | TrackingEventDto | Cache latest tracking status |
| `claim-created` | ClaimEventDto | Cache claim & invalidate baggage |

## Configuration

```yaml
server:
  port: 8086

spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: baggage-consumer-group
      
  data:
    redis:
      host: localhost
      port: 6379
```

## Run Locally

```bash
# Build lib-common first
cd ../lib-common
mvn clean install

# Build and run
cd ../kafka-consumer-service
mvn clean package
mvn spring-boot:run
```

## Docker Build

```bash
mvn clean package
docker build -t kafka-consumer-service .
docker run -p 8086:8086 kafka-consumer-service
```

## Cache Strategy

### Baggage Events
- **Created**: Cache by ID and barcode (1 hour TTL)
- **Updated**: Invalidate old cache, cache new data, invalidate related caches (passenger, flight)

### Tracking Events
- **Updated**: Invalidate tracking history, cache latest status

### Claim Events
- **Created**: Cache claim, invalidate related baggage cache

## Monitoring

Check logs for event processing:
```bash
tail -f logs/kafka-consumer-service.log
```

## Dependencies

- Spring Boot 3.5.0
- Spring Kafka
- Spring Data Redis
- lib-common (internal)
