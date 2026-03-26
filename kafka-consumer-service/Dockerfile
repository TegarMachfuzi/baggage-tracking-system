FROM openjdk:17-slim
WORKDIR /app
COPY target/kafka-consumer-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
