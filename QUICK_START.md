# Quick Start Guide

## 🚀 One-Command Setup

### Linux/Mac:
```bash
./setup.sh
```

### Windows:
```bash
setup.bat
```

This will:
1. ✅ Start Docker infrastructure (PostgreSQL, Kafka, Redis)
2. ✅ Build and install lib-common to local Maven repository
3. ✅ Verify all services are running

---

## 📋 Manual Setup (Step by Step)

### 1. Start Infrastructure
```bash
docker-compose up -d
```

Verify:
```bash
docker ps
```

Should see:
- postgres (port 5432)
- kafka (port 9092)
- zookeeper (port 2181)
- redis (port 6379)

### 2. Build lib-common
```bash
cd lib-common
mvn clean install
```

Verify:
```bash
ls ~/.m2/repository/com/baggage/lib-common/0.0.1-SNAPSHOT/
# Should see: lib-common-0.0.1-SNAPSHOT.jar
```

### 3. Run Services

**Terminal 1 - Baggage Service:**
```bash
cd baggage-service
mvn spring-boot:run
```

**Terminal 2 - Passenger Service (when ready):**
```bash
cd passenger-service
mvn spring-boot:run
```

---

## 🧪 Test Setup

### Test Infrastructure
```bash
# Test PostgreSQL
docker exec -it postgres psql -U postgres -c "SELECT version();"

# Test Redis
docker exec -it redis redis-cli PING
# Should return: PONG

# Test Kafka
docker exec -it kafka kafka-topics.sh --list --bootstrap-server localhost:9092
```

### Test lib-common
```bash
# Check if installed
ls ~/.m2/repository/com/baggage/lib-common/0.0.1-SNAPSHOT/

# Should see:
# lib-common-0.0.1-SNAPSHOT.jar
# lib-common-0.0.1-SNAPSHOT.pom
```

### Test Baggage Service
```bash
# Create baggage
curl -X POST http://localhost:8081/api/baggage \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": "123e4567-e89b-12d3-a456-426614174000",
    "flightNumber": "GA123",
    "origin": "CGK",
    "destination": "DPS"
  }'
```

---

## 🛑 Stop Services

### Stop Infrastructure
```bash
docker-compose down
```

### Stop Spring Boot Services
Press `Ctrl+C` in each terminal

---

## 🔄 Update lib-common

When you modify lib-common:

```bash
# 1. Rebuild lib-common
cd lib-common
mvn clean install

# 2. Restart services that use it
cd ../baggage-service
# Press Ctrl+C to stop
mvn spring-boot:run
```

---

## 📊 Service Ports

| Service | Port | URL |
|---------|------|-----|
| PostgreSQL | 5432 | jdbc:postgresql://localhost:5432/baggage_db |
| Kafka | 9092 | localhost:9092 |
| Zookeeper | 2181 | localhost:2181 |
| Redis | 6379 | localhost:6379 |
| Baggage Service | 8081 | http://localhost:8081 |
| Passenger Service | 8082 | http://localhost:8082 |
| Tracking Service | 8083 | http://localhost:8083 |

---

## ❓ Troubleshooting

### Docker services not starting?
```bash
# Check Docker is running
docker info

# Check logs
docker-compose logs

# Restart
docker-compose down
docker-compose up -d
```

### lib-common not found?
```bash
# Rebuild and install
cd lib-common
mvn clean install -U

# Check installation
ls ~/.m2/repository/com/baggage/lib-common/
```

### Port already in use?
```bash
# Find process using port
lsof -i :8081  # Mac/Linux
netstat -ano | findstr :8081  # Windows

# Kill process or change port in application.properties
```

### Maven build fails?
```bash
# Clean Maven cache
mvn dependency:purge-local-repository

# Rebuild
mvn clean install
```

---

## 📝 Development Workflow

```
1. Start infrastructure (once)
   └─> docker-compose up -d

2. Build lib-common (once, or when changed)
   └─> cd lib-common && mvn clean install

3. Develop services
   ├─> Edit code
   ├─> mvn spring-boot:run
   └─> Test with Postman/curl

4. If lib-common changed
   ├─> cd lib-common && mvn clean install
   └─> Restart services
```

---

**Ready to develop! 🚀**
