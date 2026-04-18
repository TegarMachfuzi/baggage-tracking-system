FROM eclipse-temurin:17-jre
WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs && chmod 777 /app/logs

COPY target/baggage-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
