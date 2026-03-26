#!/bin/bash

echo "========================================="
echo "Starting Notification Service"
echo "========================================="

# Check if lib-common is installed
if [ ! -f "../lib-common/target/lib-common-0.0.1-SNAPSHOT.jar" ]; then
    echo "lib-common not found. Building..."
    cd ../lib-common
    mvn clean install -DskipTests
    cd ../notification-service
fi

# Build if target doesn't exist
if [ ! -f "target/notification-service-0.0.1-SNAPSHOT.jar" ]; then
    echo "Building notification-service..."
    mvn clean package -DskipTests
fi

echo ""
echo "Starting service on port 8085..."
echo "Email notifications: DISABLED (set MAIL_USERNAME & MAIL_PASSWORD to enable)"
echo "SMS notifications: DISABLED"
echo ""

# Create logs directory
mkdir -p logs

# Run the service
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092 mvn spring-boot:run
