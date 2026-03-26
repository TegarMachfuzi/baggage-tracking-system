#!/bin/bash

echo "🚀 Starting Baggage Tracking System..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

echo "✅ Docker is running"
echo ""

# Start infrastructure
echo "📦 Starting infrastructure services..."
cd "/Users/m/Documents/Personal Project/baggage-tracking-system"
docker compose up -d

echo ""
echo "⏳ Waiting for services to be ready (15 seconds)..."
sleep 15

echo ""
echo "📊 Infrastructure status:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "postgres|kafka|redis|zookeeper"

echo ""
echo "✅ Infrastructure ready!"
echo ""
echo "🔨 Starting baggage-service..."
echo ""

cd baggage-service
mvn spring-boot:run
