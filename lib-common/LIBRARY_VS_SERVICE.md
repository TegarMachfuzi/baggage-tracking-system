# lib-common: Library vs Service

## 🎯 Konsep Penting

### ❌ lib-common BUKAN Service
```
Service (Running Application):
┌─────────────────────────┐
│   baggage-service       │
│                         │
│  • Main class ✅        │
│  • Listen port 8081 ✅  │
│  • Running process ✅   │
│  • Handle requests ✅   │
│  • Need Docker? YES ✅  │
└─────────────────────────┘
```

### ✅ lib-common ADALAH Library
```
Library (JAR file):
┌─────────────────────────┐
│      lib-common         │
│                         │
│  • Main class ❌        │
│  • Listen port ❌       │
│  • Running process ❌   │
│  • Handle requests ❌   │
│  • Need Docker? NO ❌   │
│                         │
│  Hanya berisi:          │
│  • Classes              │
│  • Interfaces           │
│  • Utilities            │
│  • Configurations       │
└─────────────────────────┘
```

---

## 🏗️ Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    DEVELOPMENT MACHINE                   │
│                                                          │
│  ┌────────────────────────────────────────────────┐    │
│  │     Local Maven Repository (~/.m2/)            │    │
│  │                                                 │    │
│  │  com/baggage/lib-common/0.0.1-SNAPSHOT/       │    │
│  │    └── lib-common-0.0.1-SNAPSHOT.jar          │    │
│  │         (Entities, DTOs, Utils, Configs)      │    │
│  └────────────────────────────────────────────────┘    │
│                        ↑                                 │
│                        │ mvn clean install               │
│                        │                                 │
│  ┌────────────────────┴────────────────────────────┐   │
│  │         lib-common (Source Code)                │   │
│  │  • src/main/java/com/baggage/                  │   │
│  │    ├── model/                                   │   │
│  │    ├── dto/                                     │   │
│  │    ├── mapper/                                  │   │
│  │    ├── util/                                    │   │
│  │    └── config/                                  │   │
│  └─────────────────────────────────────────────────┘   │
│                                                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │              DOCKER CONTAINERS                   │  │
│  │                                                   │  │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐      │  │
│  │  │PostgreSQL│  │  Kafka   │  │  Redis   │      │  │
│  │  │:5432     │  │  :9092   │  │  :6379   │      │  │
│  │  └──────────┘  └──────────┘  └──────────┘      │  │
│  │                                                   │  │
│  │  Infrastructure Services (Running in Docker)    │  │
│  └──────────────────────────────────────────────────┘  │
│                        ↑                                 │
│                        │ Connect via localhost           │
│                        │                                 │
│  ┌────────────────────┴────────────────────────────┐   │
│  │         MICROSERVICES (Running locally)         │   │
│  │                                                  │   │
│  │  ┌─────────────────┐    ┌─────────────────┐   │   │
│  │  │ baggage-service │    │passenger-service│   │   │
│  │  │   Port: 8081    │    │   Port: 8082    │   │   │
│  │  │                 │    │                 │   │   │
│  │  │  import from:   │    │  import from:   │   │   │
│  │  │  lib-common ✅  │    │  lib-common ✅  │   │   │
│  │  └─────────────────┘    └─────────────────┘   │   │
│  │                                                  │   │
│  │  mvn spring-boot:run (Running as Java process) │   │
│  └──────────────────────────────────────────────────┘  │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

---

## 🔄 Build & Deploy Flow

### Step 1: Build lib-common
```
┌─────────────────┐
│  lib-common/    │
│  src/main/java  │
└────────┬────────┘
         │
         │ mvn clean install
         ↓
┌─────────────────────────────────┐
│  Compile Java → JAR file        │
└────────┬────────────────────────┘
         │
         ↓
┌─────────────────────────────────┐
│  Install to ~/.m2/repository/   │
│  lib-common-0.0.1-SNAPSHOT.jar  │
└─────────────────────────────────┘
```

### Step 2: Services Use lib-common
```
┌─────────────────────────────────┐
│  baggage-service/pom.xml        │
│                                 │
│  <dependency>                   │
│    <groupId>com.baggage</...>   │
│    <artifactId>lib-common</...> │
│  </dependency>                  │
└────────┬────────────────────────┘
         │
         │ Maven resolves dependency
         ↓
┌─────────────────────────────────┐
│  Load from ~/.m2/repository/    │
│  lib-common-0.0.1-SNAPSHOT.jar  │
└────────┬────────────────────────┘
         │
         ↓
┌─────────────────────────────────┐
│  Service can import:            │
│  • com.baggage.model.*          │
│  • com.baggage.dto.*            │
│  • com.baggage.util.*           │
│  • etc.                         │
└─────────────────────────────────┘
```

---

## 📦 What Runs in Docker?

### ✅ Infrastructure (Docker)
```yaml
docker-compose.yml:
  postgres:     # Database server
  kafka:        # Message broker
  zookeeper:    # Kafka coordinator
  redis:        # Cache server
```

### ✅ Microservices (Local Java Process)
```
Terminal 1: mvn spring-boot:run  # baggage-service
Terminal 2: mvn spring-boot:run  # passenger-service
Terminal 3: mvn spring-boot:run  # tracking-service
```

### ❌ Libraries (NOT Running)
```
lib-common:  Just a JAR file
             No process
             No Docker needed
```

---

## 🎓 Analogy

Think of it like this:

```
lib-common = Perpustakaan (Library)
  • Berisi buku-buku (classes, utilities)
  • Tidak perlu "dijalankan"
  • Orang datang untuk "pinjam" (import)

baggage-service = Toko (Service)
  • Buka 24/7 (running process)
  • Melayani customer (handle requests)
  • Pakai buku dari perpustakaan (import lib-common)
```

---

## ✅ Correct Setup

```bash
# 1. Start infrastructure (Docker)
docker-compose up -d
# → PostgreSQL, Kafka, Redis running in containers

# 2. Build library (Maven)
cd lib-common
mvn clean install
# → JAR file installed to ~/.m2/repository/

# 3. Run services (Java process)
cd ../baggage-service
mvn spring-boot:run
# → Service running on port 8081, imports lib-common
```

---

## ❌ Wrong Approach

```bash
# ❌ SALAH: Trying to run lib-common
cd lib-common
mvn spring-boot:run
# ERROR: No main class found!

# ❌ SALAH: Trying to dockerize lib-common
docker run lib-common
# ERROR: Nothing to run!
```

---

## 🎯 Summary

| Aspect | lib-common | baggage-service |
|--------|-----------|-----------------|
| Type | Library (JAR) | Application (Service) |
| Has main class? | ❌ No | ✅ Yes |
| Runs as process? | ❌ No | ✅ Yes |
| Listens to port? | ❌ No | ✅ Yes (8081) |
| Need Docker? | ❌ No | Optional |
| How to use? | `mvn install` | `mvn spring-boot:run` |
| Location | `~/.m2/repository/` | Running in memory |

---

**lib-common = JAR library, NOT a running service!** ✅
