#!/bin/bash

echo "========================================="
echo "Starting Kafka Consumer Service"
echo "========================================="

# Check if lib-common is installed
if [ ! -f "../lib-common/target/lib-common-0.0.1-SNAPSHOT.jar" ]; then
    echo "lib-common not found. Building..."
    cd ../lib-common
    mvn clean install -DskipTests
    cd ../kafka-consumer-service
fi

# Build if target doesn't exist
if [ ! -f "target/kafka-consumer-service-0.0.1-SNAPSHOT.jar" ]; then
    echo "Building kafka-consumer-service..."
    mvn clean package -DskipTests
fi

echo ""
echo "Starting service on port 8086..."
echo "Logs will be written to logs/kafka-consumer-service.log"
echo ""

# Create logs directory
mkdir -p logs

# Run the service
mvn spring-boot:run
