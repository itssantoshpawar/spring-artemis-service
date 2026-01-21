# Spring Artemis Service - WebLogic to Artemis JMS Migration POC

## Overview

This is a Proof of Concept (POC) demonstrating the migration from WebLogic JMS to Apache ActiveMQ Artemis JMS. The project consists of a multi-component Spring Boot application showcasing:

1. **Component 1**: Acts as a message adapter between WebLogic and Artemis, exposing REST API for message production
2. **Component 2**: Consumes messages from Artemis queues
3. **Component 3**: Sends messages to Component 4 via Artemis queues (component3-to-component4 queue)
4. **Component 4**: Consumes messages indirectly through Component 1 or Component 3 using queue-to-queue communication

## Architecture

```
┌─────────────┐
│   Postman   │
│  (REST API) │
└──────┬──────┘
       │
       ▼
┌──────────────────────────────────┐
│      Component 1 (Port 8081)     │
│   WebLogic to Artemis Adapter    │
│  - REST API Controller           │
│  - WebLogic Listener (Optional)  │
│  - Message Forwarder             │
└─────────┬────────────────────────┘
          │
          ├─────► component2.queue ─────► Component 2 (Port 8082)
          │
          └─────► component4.queue ─────► Component 4 (Port 8084)
                                             ▲
                                             │
┌─────────────────────────────────┐          │
│      Component 3 (Port 8083)    │          │
│   Producer/Consumer Service     │──────────┘
│  - Listens: component3-to-component4       
│  - Sends: component4.queue                  
└─────────────────────────────────┘
```

## Technologies

- **Spring Boot 3.2.0**
- **Apache ActiveMQ Artemis 2.28.0**
- **Java 17**
- **Maven**
- **Docker & Docker Compose**

## Project Structure

```
spring-artemis-service/
├── component-1/              # Adapter Service
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── component-2/              # Consumer Service
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── component-3/              # Producer/Consumer Service
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── component-4/              # Consumer Service
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── artemis-config/           # Artemis broker configuration
│   └── broker.xml
├── postman/                  # Postman collection for testing
│   └── Artemis-JMS-POC.postman_collection.json
├── docker-compose.yml        # Docker orchestration
├── pom.xml                   # Parent POM
├── build-all.bat            # Build script
├── run-component1.bat       # Run Component 1 locally
├── run-component2.bat       # Run Component 2 locally
├── run-component3.bat       # Run Component 3 locally
├── run-component4.bat       # Run Component 4 locally
├── run-docker.bat           # Run all in Docker
└── stop-docker.bat          # Stop Docker containers
```

## Prerequisites

### For Local Development
- Java 17 or higher
- Maven 3.6+
- Apache ActiveMQ Artemis (running locally or in Docker)

### For Docker Deployment
- Docker Desktop (Windows)
- Docker Compose

## Getting Started

### Option 1: Run Locally (Windows)

#### Step 1: Build All Components
```batch
build-all.bat
```

#### Step 2: Start Artemis Broker
Make sure Artemis is running on `tcp://localhost:61616`

You can use Docker to run Artemis:
```bash
docker run -d --name artemis \
  -e AMQ_USER=admin \
  -e AMQ_PASSWORD=admin \
  -p 61616:61616 \
  -p 8161:8161 \
  quay.io/artemiscloud/activemq-artemis-broker:1.0.16
```

#### Step 3: Run Components (in separate terminals)
```batch
# Terminal 1 - Run Component 1
run-component1.bat

# Terminal 2 - Run Component 2
run-component2.bat

# Terminal 3 - Run Component 3
run-component3.bat

# Terminal 4 - Run Component 4
run-component4.bat
```

### Option 2: Run with Docker (Recommended)

#### Build and Run All Services
```batch
run-docker.bat
```

This will:
1. Build all Maven projects
2. Build Docker images
3. Start Artemis broker
4. Start all three components

#### Stop All Services
```batch
stop-docker.bat
```

## Testing with Postman

### Import Postman Collection
1. Open Postman
2. Click **Import** → **File**
3. Select `postman/Artemis-JMS-POC.postman_collection.json`
4. The collection will be imported with pre-configured requests

### Available Endpoints

#### Component 1 - REST API (Port 8081)

**Send XML Message to Component 2 Queue**
```
POST http://localhost:8081/api/messages/send?queue=component2.queue
Content-Type: application/xml

<?xml version="1.0" encoding="UTF-8"?>
<order>
    <orderId>ORD-12345</orderId>
    <customer>
        <name>John Doe</name>
    </customer>
    <totalAmount>2149.93</totalAmount>
</order>
```

**Send XML Message to Component 4 Queue**
```
POST http://localhost:8081/api/messages/send?queue=component4.queue
Content-Type: application/xml

<transaction>
    <transactionId>TXN-98765</transactionId>
    <amount>1500.50</amount>
</transaction>
```

**Health Check**
```
GET http://localhost:8081/api/messages/health
```

### Monitor Messages

#### Console Logs
Watch the console output for all three components to see:
- Component 1: Sending messages
- Component 2: Receiving messages from component2.queue
- Component 4: Receiving messages from component4.queue

#### Artemis Web Console
```
http://localhost:8161/console
Username: admin
Password: admin
```

## Configuration

### Component 1 (application.yml)
```yaml
server:
  port: 8081

artemis:
  broker:
    url: ${ARTEMIS_BROKER_URL:tcp://localhost:61616}
    user: ${ARTEMIS_BROKER_USER:admin}
    password: ${ARTEMIS_BROKER_PASSWORD:admin}

# Optional WebLogic Configuration
weblogic:
  jms:
    url: ${WEBLOGIC_URL:t3://localhost:7001}
    username: ${WEBLOGIC_USERNAME:weblogic}
    password: ${WEBLOGIC_PASSWORD:welcome1}
```

### Environment Variables

For Docker deployment, configure in `docker-compose.yml`:
```yaml
environment:
  - ARTEMIS_BROKER_URL=tcp://artemis:61616
  - ARTEMIS_BROKER_USER=admin
  - ARTEMIS_BROKER_PASSWORD=admin
```

## Key Features

### 1. Dual JMS Support (Component 1)
- Connects to both WebLogic and Artemis brokers
- WebLogic configuration is optional (conditional bean creation)
- Forwards messages from WebLogic to Artemis

### 2. REST API for Message Production
- Send XML payloads via HTTP POST
- Support for JSON and plain text messages
- Configurable queue destinations

### 3. Message Logging
- All components log received messages
- Detailed message content and metadata logging
- Timestamps for tracking message flow

### 4. Docker Support
- Complete Docker Compose setup
- Pre-configured Artemis broker
- Network isolation
- Health checks for service dependencies

### 5. Windows Batch Scripts
- Easy build and run commands
- Separate scripts for each component
- Docker orchestration script

## Queue Configuration

The following queues are configured in Artemis:
- `component2.queue` - Messages for Component 2
- `component3-to-component4` - Messages from Component 3 to Component 4
- `component4.queue` - Messages for Component 4
- `weblogic.input.queue` - Optional queue for WebLogic integration

## Troubleshooting

### Component won't start
- Ensure Artemis is running and accessible
- Check port conflicts (8081, 8082, 8084)
- Verify Maven build completed successfully

### Messages not received
- Check queue names match configuration
- Verify Artemis is running (`http://localhost:8161/console`)
- Check console logs for connection errors

### Docker issues
- Ensure Docker Desktop is running
- Check `docker-compose logs` for errors
- Verify ports 61616, 8081, 8082, 8084, 8161 are available

## Building from Source

```bash
# Build parent and all modules
mvn clean install

# Build specific component
cd component-1
mvn clean package

# Skip tests
mvn clean install -DskipTests
```

## Running Tests

```bash
# Run all tests
mvn test

# Run tests for specific component
cd component-2
mvn test
```

## WebLogic Integration (Optional)

To enable WebLogic integration in Component 1:

1. Configure WebLogic JMS connection in `application.yml`
2. Ensure WebLogic server is accessible
3. Component 1 will listen to WebLogic queue and forward to Artemis

If WebLogic is not available, Component 1 runs in Artemis-only mode.

## License

This is a POC project for demonstration purposes.

## Support

For issues or questions, please refer to the project documentation or contact the development team.
