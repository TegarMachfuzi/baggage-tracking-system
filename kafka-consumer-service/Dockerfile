# Gunakan base image OpenJDK (sesuaikan versi Java dengan projectmu)
FROM openjdk:17-jdk-slim

# Buat direktori aplikasi di dalam container
WORKDIR /app

# Copy file jar hasil build dari target folder (pastikan sudah build jar dengan mvn package)
COPY target/*.jar app.jar

# Jalankan aplikasi Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
