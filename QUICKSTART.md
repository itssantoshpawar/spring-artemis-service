# Quick Start Guide

## Prerequisites
- Java 11 or higher
- Maven 3.6+
- Docker Desktop (for Docker deployment)

## Option 1: Quick Start with Docker (Recommended)

### 1. Build the project
```bash
# Windows
build-all.bat

# Linux/Mac
mvn clean install -DskipTests
```

### 2. Start all services with Docker
```bash
# Windows
run-docker.bat

# Linux/Mac
docker-compose up --build
```

This will start:
- Artemis Broker on ports 61616 (JMS) and 8161 (Web Console)
- Component 1 on port 8081
- Component 2 on port 8082
- Component 4 on port 8084

### 3. Test with curl or Postman

**Send a message:**
```bash
curl -X POST http://localhost:8081/api/messages/send?queue=component2.queue \
  -H "Content-Type: application/xml" \
  -d '<?xml version="1.0" encoding="UTF-8"?><order><orderId>ORD-123</orderId><customer><name>John Doe</name></customer><totalAmount>99.99</totalAmount></order>'
```

**Check health:**
```bash
curl http://localhost:8081/api/messages/health
```

### 4. Monitor messages
- Watch Component 1 logs to see message sending
- Watch Component 2 logs to see message consumption from component2.queue
- Watch Component 4 logs to see message consumption from component4.queue
- Access Artemis Web Console at http://localhost:8161/console (admin/admin)

### 5. Stop services
```bash
# Windows
stop-docker.bat

# Linux/Mac
docker-compose down
```

## Option 2: Run Locally

### 1. Start Artemis Broker
```bash
docker run -d --name artemis \
  -e AMQ_USER=admin \
  -e AMQ_PASSWORD=admin \
  -p 61616:61616 \
  -p 8161:8161 \
  quay.io/artemiscloud/activemq-artemis-broker:1.0.16
```

### 2. Build all components
```bash
# Windows
build-all.bat

# Linux/Mac
mvn clean install -DskipTests
```

### 3. Start components (in separate terminals)
```bash
# Terminal 1 - Component 1
# Windows
run-component1.bat

# Linux/Mac
cd component-1
java -jar target/component-1-1.0.0-SNAPSHOT.jar --spring.profiles.active=artemis-only

# Terminal 2 - Component 2
# Windows
run-component2.bat

# Linux/Mac
cd component-2
java -jar target/component-2-1.0.0-SNAPSHOT.jar

# Terminal 3 - Component 4
# Windows
run-component4.bat

# Linux/Mac
cd component-4
java -jar target/component-4-1.0.0-SNAPSHOT.jar
```

### 4. Test and monitor as described in Docker option

## Testing with Postman

1. Import the Postman collection from `postman/Artemis-JMS-POC.postman_collection.json`
2. The collection includes:
   - Send XML to Component 2 Queue
   - Send XML to Component 4 Queue
   - Send JSON Message
   - Health Check
   - Large XML Order
   - Artemis Web Console link

## Expected Behavior

1. When you send a message to Component 1 via REST API:
   - Component 1 receives the HTTP POST request
   - Component 1 logs the message and sends it to Artemis queues
   - Component 2 receives and logs the message from component2.queue
   - Component 4 receives and logs the message from component4.queue

2. All message flows are logged with timestamps for tracking

## Troubleshooting

### Port conflicts
If ports are in use, you can change them in:
- `component-X/src/main/resources/application.yml` for local runs
- `docker-compose.yml` for Docker runs

### Connection refused
- Ensure Artemis is running: `docker ps` should show artemis container
- Check Artemis logs: `docker logs artemis`
- Verify port 61616 is accessible

### Build failures
- Ensure Java 11+ is installed: `java -version`
- Ensure Maven is installed: `mvn -version`
- Clean and rebuild: `mvn clean install -U`

## WebLogic Integration (Optional)

To enable WebLogic JMS integration in Component 1:

1. Edit `component-1/src/main/resources/application.yml`
2. Uncomment the WebLogic configuration section
3. Update the WebLogic connection details:
   ```yaml
   weblogic:
     jms:
       url: t3://your-weblogic-server:7001
       username: your-username
       password: your-password
   ```
4. Add WebLogic client library to dependencies
5. Rebuild and restart Component 1

## Next Steps

- Explore the Artemis Web Console to see queues and messages
- Try sending different XML/JSON payloads
- Modify the message listeners to add custom business logic
- Scale consumers by increasing concurrency in configuration
