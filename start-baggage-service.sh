#!/bin/bash

echo "🚀 Starting Baggage Tracking System..."
echo ""

# Check if Podman is running
if ! podman info > /dev/null 2>&1; then
    echo "❌ Podman is not running. Please start Podman first."
    exit 1
fi

echo "✅ Podman is running"
echo ""

# Start infrastructure
echo "📦 Starting infrastructure services..."
cd "/Users/tegarmachfudzi/Documents/Personal Projek/baggage-tracking-system"
podman-compose up -d

echo ""
echo "⏳ Waiting for services to be ready (15 seconds)..."
sleep 15

echo ""
echo "📊 Infrastructure status:"
podman ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "postgres|kafka|redis|zookeeper"

echo ""
echo "✅ Infrastructure ready!"
echo ""
echo "🔨 Starting baggage-service..."
echo ""

cd baggage-service
mvn spring-boot:run
