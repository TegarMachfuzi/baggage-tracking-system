#!/bin/bash

echo "🔧 Quick Fix - Build & Deploy to OpenShift"
echo ""

# Get current project
PROJECT=$(oc project -q)
echo "📦 Project: $PROJECT"
echo ""

# Build application
echo "1️⃣ Building JAR..."
cd baggage-service
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi

echo "✅ JAR built"
echo ""

# Create BuildConfig if not exists
echo "2️⃣ Creating BuildConfig..."
oc get bc baggage-service &> /dev/null
if [ $? -ne 0 ]; then
    oc new-build --name baggage-service --binary --strategy docker
    echo "✅ BuildConfig created"
else
    echo "✅ BuildConfig exists"
fi

echo ""

# Build image
echo "3️⃣ Building container image..."
oc start-build baggage-service --from-dir=. --follow

if [ $? -ne 0 ]; then
    echo "❌ Image build failed"
    exit 1
fi

echo "✅ Image built"
echo ""

# Deploy infrastructure
echo "4️⃣ Deploying infrastructure..."
cd k8s

# Create secret and configmap
oc apply -f configmap.yaml

# Deploy Kafka & Zookeeper
oc apply -f kafka.yaml
echo "⏳ Waiting for Kafka..."
sleep 10

# Deploy PostgreSQL
oc apply -f postgres.yaml
echo "⏳ Waiting for PostgreSQL..."
sleep 5

# Deploy Redis
oc apply -f redis.yaml
echo "⏳ Waiting for Redis..."
sleep 5

echo "✅ Infrastructure deployed"
echo ""

# Deploy application
echo "5️⃣ Deploying baggage-service..."
oc apply -f deployment.yaml

echo ""
echo "⏳ Waiting for deployment..."
oc rollout status deployment/baggage-service --timeout=5m

if [ $? -ne 0 ]; then
    echo "❌ Deployment failed"
    echo ""
    echo "📋 Check logs:"
    echo "   oc logs -f deployment/baggage-service"
    echo ""
    echo "📊 Check pods:"
    oc get pods -l app=baggage-service
    exit 1
fi

echo ""
echo "✅ Deployment successful!"
echo ""
echo "📊 Status:"
oc get pods -l app=baggage-service
echo ""

ROUTE=$(oc get route baggage-service -o jsonpath='{.spec.host}' 2>/dev/null)
if [ ! -z "$ROUTE" ]; then
    echo "🌐 Application URL: https://$ROUTE"
    echo ""
    echo "🧪 Test endpoints:"
    echo "   curl https://$ROUTE/actuator/health"
    echo "   curl https://$ROUTE/api/baggage"
else
    echo "⚠️  Route not found. Create route:"
    echo "   oc expose svc/baggage-service"
fi

echo ""
echo "📝 Useful commands:"
echo "   oc logs -f deployment/baggage-service"
echo "   oc get pods"
echo "   oc describe pod <pod-name>"
echo ""
echo "✅ Done!"
