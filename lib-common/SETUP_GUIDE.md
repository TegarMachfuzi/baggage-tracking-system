# lib-common Setup Guide

## ❓ Kenapa lib-common TIDAK perlu Docker?

**lib-common adalah LIBRARY, bukan SERVICE.**

- ❌ Tidak perlu running/dijalankan
- ❌ Tidak punya main class
- ❌ Tidak listen ke port
- ✅ Hanya berisi kode yang di-import oleh service lain

---

## ✅ Cara Setup lib-common (RECOMMENDED)

### **Step 1: Build & Install ke Local Maven Repository**

```bash
cd lib-common
mvn clean install
```

**Output:**
```
[INFO] Installing lib-common-0.0.1-SNAPSHOT.jar to ~/.m2/repository/com/baggage/lib-common/0.0.1-SNAPSHOT/
[INFO] BUILD SUCCESS
```

### **Step 2: Service Lain Otomatis Bisa Akses**

Setiap service yang punya dependency ini di `pom.xml`:

```xml
<dependency>
    <groupId>com.baggage</groupId>
    <artifactId>lib-common</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Akan otomatis download dari `~/.m2/repository/`

---

## 🔄 Workflow Development

### Saat Develop lib-common:

```bash
# 1. Edit code di lib-common
# 2. Build & install ulang
cd lib-common
mvn clean install

# 3. Restart service yang pakai lib-common
cd ../baggage-service
mvn spring-boot:run
```

### Saat Develop Service:

```bash
# Langsung run, tidak perlu install lib-common lagi
cd baggage-service
mvn spring-boot:run
```

---

## 🐳 Docker Compose (Infrastructure Only)

Docker compose hanya untuk **infrastructure services**:

```yaml
services:
  postgres:    # Database
  kafka:       # Message broker
  zookeeper:   # Kafka dependency
  redis:       # Cache
  
  # ❌ TIDAK ADA lib-common (karena bukan service)
```

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────┐
│         Local Maven Repository          │
│         ~/.m2/repository/               │
│                                         │
│  com/baggage/lib-common/               │
│    └── 0.0.1-SNAPSHOT/                 │
│         └── lib-common-0.0.1.jar       │
└─────────────────────────────────────────┘
                    ↑
                    │ mvn clean install
                    │
┌───────────────────┴─────────────────────┐
│           lib-common (source)           │
│  • Entities                             │
│  • DTOs                                 │
│  • Mappers                              │
│  • Utils                                │
│  • Configs                              │
└─────────────────────────────────────────┘
                    ↓
        ┌───────────┴───────────┐
        │                       │
┌───────▼──────┐      ┌────────▼────────┐
│   baggage-   │      │   passenger-    │
│   service    │      │   service       │
│              │      │                 │
│ (depends on  │      │ (depends on     │
│  lib-common) │      │  lib-common)    │
└──────────────┘      └─────────────────┘
```

---

## 🚀 Quick Start

### 1. Setup Infrastructure
```bash
# Start Docker services
docker-compose up -d

# Verify
docker ps
# Should see: postgres, kafka, zookeeper, redis
```

### 2. Build lib-common
```bash
cd lib-common
mvn clean install
```

### 3. Run Services
```bash
# Terminal 1: baggage-service
cd baggage-service
mvn spring-boot:run

# Terminal 2: passenger-service
cd passenger-service
mvn spring-boot:run
```

---

## 🔧 Advanced: Private Maven Repository (Optional)

Jika mau setup seperti production, bisa pakai Nexus:

### Enable Nexus di docker-compose.yml
```yaml
nexus:
  image: sonatype/nexus3:latest
  ports:
    - "8081:8081"
  volumes:
    - nexus_data:/nexus-data
```

### Configure Maven settings.xml
```xml
<settings>
  <servers>
    <server>
      <id>nexus</id>
      <username>admin</username>
      <password>admin123</password>
    </server>
  </servers>
</settings>
```

### Deploy lib-common ke Nexus
```bash
cd lib-common
mvn clean deploy
```

**Tapi untuk development lokal, `mvn install` sudah cukup!**

---

## ❓ FAQ

**Q: Kenapa lib-common tidak di Docker?**  
A: Karena ini library, bukan aplikasi yang running. Library hanya di-compile dan di-import.

**Q: Bagaimana service lain bisa akses lib-common?**  
A: Via Maven dependency. Setelah `mvn install`, JAR tersimpan di `~/.m2/repository/`

**Q: Apakah perlu restart service saat update lib-common?**  
A: Ya. Setelah `mvn clean install` di lib-common, restart service yang menggunakannya.

**Q: Bisa pakai Docker untuk lib-common?**  
A: Bisa, tapi tidak perlu. Itu untuk advanced setup dengan private Maven repository.

---

## ✅ Checklist Setup

- [x] Docker compose running (postgres, kafka, redis)
- [x] lib-common: `mvn clean install` ✅
- [x] baggage-service: add dependency di pom.xml ✅
- [x] baggage-service: `mvn spring-boot:run` ✅

---

## 📝 Summary

**lib-common = JAR library**
- Build: `mvn clean install`
- Location: `~/.m2/repository/`
- Usage: Maven dependency di service lain

**Docker = Infrastructure services**
- PostgreSQL
- Kafka + Zookeeper
- Redis

**Tidak perlu Docker untuk lib-common!** ✅

---

**Ready to continue development!** 🚀
