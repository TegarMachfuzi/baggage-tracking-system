# Kafka Consumer Service - Project Structure

```
kafka-consumer-service/
├── src/main/java/com/baggage/
│   ├── kafka/
│   │   ├── KafkaConsumerApplication.java    # Main application
│   │   ├── config/
│   │   │   └── KafkaConsumerConfig.java     # Kafka consumer config
│   │   ├── consumer/
│   │   │   ├── BaggageEventConsumer.java    # Baggage events
│   │   │   ├── TrackingEventConsumer.java   # Tracking events
│   │   │   └── ClaimEventConsumer.java      # Claim events
│   │   └── service/
│   │       └── CacheService.java            # Redis cache operations
│   └── Main.java                            # Legacy main (unused)
├── src/main/resources/
│   ├── application.yml                      # Configuration
│   └── application.properties               # Alternative config
├── k8s/
│   └── deployment.yaml                      # Kubernetes manifest
├── Dockerfile                               # Docker image
├── pom.xml                                  # Maven dependencies
├── README.md                                # Documentation
├── IMPLEMENTATION_SUMMARY.md                # Implementation details
├── start-consumer.sh                        # Quick start script
└── test-consumer.sh                         # Test script
```

## Key Files

### Consumers
- **BaggageEventConsumer.java** - Handles baggage-created, baggage-updated
- **TrackingEventConsumer.java** - Handles tracking-updated
- **ClaimEventConsumer.java** - Handles claim-created

### Services
- **CacheService.java** - Redis operations (cache, invalidate, pattern invalidate)

### Configuration
- **KafkaConsumerConfig.java** - Kafka consumer factory & listener
- **application.yml** - Port, Kafka, Redis config

### Scripts
- **start-consumer.sh** - Build & run service
- **test-consumer.sh** - End-to-end test flow
