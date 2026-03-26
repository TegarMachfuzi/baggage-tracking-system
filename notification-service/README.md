# Notification Service

Service untuk mengirim notifikasi email dan SMS kepada passenger berdasarkan event dari Kafka.

## Features

- ✅ Email notifications (via SMTP)
- ✅ SMS notifications (stub - ready for integration)
- ✅ Consume Kafka events (baggage-created, tracking-updated, claim-created)
- ✅ Notification templates
- ✅ Configurable enable/disable per channel
- ✅ Error handling & logging

## Events Consumed

| Topic | Event Type | Notification |
|-------|-----------|--------------|
| `baggage-created` | BaggageEventDto | Email + SMS: Baggage check-in confirmation |
| `tracking-updated` | TrackingEventDto | Email + SMS: Location update |
| `claim-created` | ClaimEventDto | Email: Claim received confirmation |

## Configuration

### application.yml

```yaml
server:
  port: 8085

spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: notification-consumer-group
      
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}

notification:
  email:
    from: noreply@baggagetracking.com
    enabled: true
  sms:
    enabled: false
```

### Email Setup (Gmail)

1. Enable 2-Factor Authentication
2. Generate App Password: https://myaccount.google.com/apppasswords
3. Set environment variables:

```bash
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
```

## Run Locally

```bash
# Build lib-common first
cd ../lib-common
mvn clean install

# Build and run
cd ../notification-service
mvn clean package

# With email enabled
MAIL_USERNAME=your-email@gmail.com \
MAIL_PASSWORD=your-app-password \
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
mvn spring-boot:run

# With email disabled (testing)
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
mvn spring-boot:run
```

## Test

### 1. Start Services

```bash
# Terminal 1: Kafka & Redis
docker-compose up -d kafka redis

# Terminal 2: Notification Service
cd notification-service
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092 mvn spring-boot:run

# Terminal 3: Baggage Service (to trigger events)
cd baggage-service
mvn spring-boot:run
```

### 2. Trigger Notification

```bash
# Create baggage (triggers baggage-created event)
curl -X POST http://localhost:8081/api/baggage \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": "550e8400-e29b-41d4-a716-446655440000",
    "flightNumber": "GA123",
    "origin": "CGK",
    "destination": "DPS"
  }'

# Check notification-service logs
# Should see: "Notifications sent for baggage-created: BAG..."
```

## Notification Templates

### Baggage Created
```
Subject: Baggage Check-in Confirmation

Dear Passenger,

Your baggage has been successfully checked in.

Details:
- Barcode: BAG20260303ABC123
- Flight: GA123
- Destination: DPS

You can track your baggage status anytime using the barcode.

Thank you for choosing our service.

Best regards,
Baggage Tracking Team
```

### Tracking Updated
```
Subject: Baggage Location Update

Dear Passenger,

Your baggage location has been updated.

Details:
- Barcode: BAG20260303ABC123
- Current Location: CGK Terminal 3
- Status: IN_TRANSIT

Track your baggage anytime using the barcode.

Best regards,
Baggage Tracking Team
```

## SMS Integration (TODO)

Currently SMS is stubbed. To integrate:

### Option 1: Twilio

```xml
<dependency>
    <groupId>com.twilio.sdk</groupId>
    <artifactId>twilio</artifactId>
    <version>9.14.1</version>
</dependency>
```

```java
Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
Message message = Message.creator(
    new PhoneNumber(to),
    new PhoneNumber(from),
    body
).create();
```

### Option 2: AWS SNS

```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>sns</artifactId>
</dependency>
```

```java
SnsClient snsClient = SnsClient.create();
PublishRequest request = PublishRequest.builder()
    .message(message)
    .phoneNumber(phoneNumber)
    .build();
snsClient.publish(request);
```

## Architecture

```
Kafka Topics                Notification Service           Channels
─────────────              ────────────────────           ─────────
baggage-created    ──→    BaggageEventConsumer    ──→    Email
                                    ↓                      SMS
tracking-updated   ──→    TrackingEventConsumer   ──→    Email
                                    ↓                      SMS
claim-created      ──→    ClaimEventConsumer      ──→    Email
```

## Monitoring

```bash
# Check logs
tail -f logs/notification-service.log

# Check if service is running
curl http://localhost:8085/actuator/health

# Check Kafka consumer group
kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
  --group notification-consumer-group --describe
```

## TODO

- [ ] Integrate with passenger-service to get real email/phone
- [ ] Implement SMS provider (Twilio/AWS SNS)
- [ ] Add notification history (store in database)
- [ ] Add retry mechanism for failed notifications
- [ ] Add notification preferences (opt-in/opt-out)
- [ ] Add HTML email templates
- [ ] Add push notifications (FCM)

## Dependencies

- Spring Boot 3.5.0
- Spring Kafka
- Spring Mail
- lib-common (BaggageEventDto, TrackingEventDto, ClaimEventDto)

## Status

**COMPLETE** - Ready for testing

- ✅ Email service implemented
- ✅ SMS service stubbed
- ✅ All event consumers implemented
- ✅ Notification templates
- ✅ Docker & K8s ready
- ✅ Documentation

---

**Last Updated**: 2026-03-03
