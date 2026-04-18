#!/bin/bash

echo "=========================================="
echo "  Baggage Tracking System - Setup"
echo "=========================================="
echo ""

# Check if Podman is running
if ! podman info > /dev/null 2>&1; then
    echo "❌ Podman is not running. Please start Podman first."
    exit 1
fi

echo "✅ Podman is running"
echo ""

# Start infrastructure
echo "🚀 Starting infrastructure services..."
podman-compose up -d

echo ""
echo "⏳ Waiting for services to be ready..."
sleep 10

# Check services
echo ""
echo "📊 Checking services status..."
podman-compose ps

echo ""
echo "=========================================="
echo "  Building lib-common"
echo "=========================================="
echo ""

cd lib-common
mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ lib-common installed successfully!"
    echo "   Location: ~/.m2/repository/com/baggage/lib-common/"
else
    echo ""
    echo "❌ Failed to build lib-common"
    exit 1
fi

cd ..

echo ""
echo "=========================================="
echo "  Setup Complete!"
echo "=========================================="
echo ""
echo "Infrastructure services running:"
echo "  • PostgreSQL  : localhost:5432"
echo "  • Kafka       : localhost:9092"
echo "  • Zookeeper   : localhost:2181"
echo "  • Redis       : localhost:6379"
echo ""
echo "lib-common installed to local Maven repository"
echo ""
echo "Next steps:"
echo "  1. cd baggage-service"
echo "  2. mvn spring-boot:run"
echo ""
echo "Or run all services:"
echo "  ./run-all-services.sh"
echo ""
