# OpenShift Deployment Guide - Baggage Service

## Prerequisites

1. OpenShift CLI (oc) installed
2. Access to OpenShift cluster
3. Docker installed (for building image)

---

## Step 1: Login to OpenShift

```bash
# Login to your OpenShift cluster
oc login --token=<your-token> --server=<your-server>

# Create new project
oc new-project baggage-tracking-system

# Or use existing project
oc project baggage-tracking-system
```

---

## Step 2: Build Application

```bash
cd baggage-service

# Build JAR
mvn clean package -DskipTests

# Verify JAR created
ls -lh target/baggage-service-0.0.1-SNAPSHOT.jar
```

---

## Step 3: Build & Push Docker Image

### Option A: Using OpenShift BuildConfig (Recommended)

```bash
# Create BuildConfig from Dockerfile
oc new-build --name baggage-service \
  --binary \
  --strategy docker

# Start build from local directory
oc start-build baggage-service \
  --from-dir=. \
  --follow

# Check build status
oc get builds
```

### Option B: Using External Registry

```bash
# Build image locally
docker build -t baggage-service:latest .

# Tag for registry
docker tag baggage-service:latest <registry>/baggage-service:latest

# Push to registry
docker push <registry>/baggage-service:latest
```

---

## Step 4: Deploy Infrastructure

```bash
cd k8s

# Deploy PostgreSQL
oc apply -f postgres.yaml

# Deploy Redis
oc apply -f redis.yaml

# Create ConfigMap and Secret
oc apply -f configmap.yaml

# Wait for pods to be ready
oc get pods -w
```

---

## Step 5: Deploy Baggage Service

```bash
# Deploy application
oc apply -f deployment.yaml

# Check deployment status
oc get deployments
oc get pods
oc get services
oc get routes

# Get application URL
oc get route baggage-service -o jsonpath='{.spec.host}'
```

---

## Step 6: Verify Deployment

```bash
# Check logs
oc logs -f deployment/baggage-service

# Check pod status
oc describe pod <pod-name>

# Test health endpoint
ROUTE=$(oc get route baggage-service -o jsonpath='{.spec.host}')
curl https://$ROUTE/actuator/health

# Test API
curl -X POST https://$ROUTE/api/baggage \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": "123e4567-e89b-12d3-a456-426614174000",
    "flightNumber": "GA123",
    "origin": "CGK",
    "destination": "DPS"
  }'
```

---

## Step 7: Scale Application

```bash
# Scale to 3 replicas
oc scale deployment/baggage-service --replicas=3

# Check scaling
oc get pods -l app=baggage-service

# Auto-scaling (optional)
oc autoscale deployment/baggage-service \
  --min=2 \
  --max=5 \
  --cpu-percent=80
```

---

## Monitoring & Troubleshooting

### View Logs
```bash
# Real-time logs
oc logs -f deployment/baggage-service

# Logs from specific pod
oc logs <pod-name>

# Previous logs (if pod crashed)
oc logs <pod-name> --previous
```

### Debug Pod
```bash
# Get shell access
oc rsh <pod-name>

# Check environment variables
oc exec <pod-name> -- env

# Port forward for local testing
oc port-forward deployment/baggage-service 8081:8081
```

### Check Resources
```bash
# Resource usage
oc adm top pods

# Events
oc get events --sort-by='.lastTimestamp'

# Describe deployment
oc describe deployment baggage-service
```

---

## Update Deployment

### Update Image
```bash
# Rebuild and push new image
oc start-build baggage-service --from-dir=. --follow

# Rollout new version
oc rollout restart deployment/baggage-service

# Check rollout status
oc rollout status deployment/baggage-service

# Rollback if needed
oc rollout undo deployment/baggage-service
```

### Update Configuration
```bash
# Edit ConfigMap
oc edit configmap baggage-service-config

# Restart pods to pick up changes
oc rollout restart deployment/baggage-service
```

---

## Clean Up

```bash
# Delete all resources
oc delete -f deployment.yaml
oc delete -f postgres.yaml
oc delete -f redis.yaml
oc delete -f configmap.yaml

# Or delete entire project
oc delete project baggage-tracking-system
```

---

## Environment Variables

The application uses these environment variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | PostgreSQL URL | jdbc:postgresql://postgres:5432/baggage_db |
| `SPRING_DATASOURCE_USERNAME` | DB username | postgres |
| `SPRING_DATASOURCE_PASSWORD` | DB password | From secret |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Kafka servers | kafka:9092 |
| `SPRING_DATA_REDIS_HOST` | Redis host | redis |
| `SPRING_DATA_REDIS_PORT` | Redis port | 6379 |

---

## Health Checks

- **Liveness**: `/actuator/health` - Checks if app is running
- **Readiness**: `/actuator/health` - Checks if app is ready to serve traffic

---

## Resource Limits

**Requests:**
- Memory: 512Mi
- CPU: 250m

**Limits:**
- Memory: 1Gi
- CPU: 500m

Adjust in `deployment.yaml` based on your needs.

---

## Security Considerations

1. ✅ Use secrets for sensitive data
2. ✅ Enable TLS on routes
3. ✅ Set resource limits
4. ✅ Use non-root user in container
5. ⚠️ Update default passwords in production
6. ⚠️ Enable network policies
7. ⚠️ Use private image registry

---

## Next Steps

1. Deploy Kafka (if needed)
2. Deploy other microservices
3. Setup monitoring (Prometheus/Grafana)
4. Configure CI/CD pipeline
5. Setup backup for PostgreSQL

---

## Useful Commands

```bash
# Quick status check
oc get all

# Watch pods
oc get pods -w

# Get route URL
oc get route baggage-service

# Stream logs
oc logs -f deployment/baggage-service

# Execute command in pod
oc exec <pod-name> -- <command>

# Copy files from pod
oc cp <pod-name>:/path/to/file ./local-file
```

---

**Ready to deploy! 🚀**
