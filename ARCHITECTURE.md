# Architecture & Design Document

## Overview

This POC demonstrates a migration strategy from WebLogic JMS to Apache ActiveMQ Artemis, featuring a multi-component Spring Boot architecture that showcases message routing, consumption, and REST API integration.

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                          External Clients                        │
│                    (Postman, REST Clients)                       │
└─────────────────────┬───────────────────────────────────────────┘
                      │ HTTP POST (XML/JSON)
                      ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Component 1 (Port 8081)                       │
│              WebLogic to Artemis Adapter Service                 │
├─────────────────────────────────────────────────────────────────┤
│ • REST API Controller (/api/messages/send)                       │
│ • WebLogic JMS Listener (Optional, Conditional)                  │
│ • Message Forwarding Service                                     │
│ • Artemis JMS Producer                                           │
└──────┬────────────────────┬─────────────────────────────────────┘
       │                    │
       │ JMS                │ JMS
       ▼                    ▼
┌──────────────────┐  ┌──────────────────┐
│ component2.queue │  │ component4.queue │
└──────┬───────────┘  └────────┬─────────┘
       │                       │
       │ JMS                   │ JMS
       ▼                       ▼
┌──────────────────┐  ┌─────────────────┐
│   Component 2    │  │   Component 4   │
│   (Port 8082)    │  │   (Port 8084)   │
│                  │  │                 │
│ Artemis Consumer │  │ Artemis Consumer│
└──────────────────┘  └─────────────────┘

                 ┌─────────────────────┐
                 │   Artemis Broker    │
                 │   (Port 61616)      │
                 │   Web UI: 8161      │
                 └─────────────────────┘
```

## Component Details

### Component 1: Adapter Service
**Port:** 8081  
**Purpose:** Acts as the central adapter between external clients, WebLogic (optional), and Artemis

#### Responsibilities:
1. **REST API Endpoint** - Accepts HTTP POST requests with XML/JSON payloads
2. **Message Validation** - Validates incoming messages
3. **WebLogic Integration** - (Optional) Listens to WebLogic JMS queues
4. **Message Forwarding** - Routes messages to appropriate Artemis queues
5. **Logging** - Comprehensive message flow logging

#### Key Classes:
- `Component1Application` - Main Spring Boot application
- `MessageController` - REST API controller for message submission
- `MessageForwardingService` - Service for sending messages to Artemis
- `ArtemisJmsConfig` - Artemis connection configuration
- `WebLogicJmsConfig` - (Conditional) WebLogic connection configuration
- `WebLogicMessageListener` - (Conditional) WebLogic queue listener

#### Configuration:
```yaml
artemis:
  broker:
    url: tcp://localhost:61616
    user: admin
    password: admin
```

### Component 2: Primary Consumer Service
**Port:** 8082  
**Purpose:** Consumes and processes messages from component2.queue

#### Responsibilities:
1. **Message Consumption** - Listens to component2.queue
2. **Message Processing** - Processes business logic
3. **Logging** - Logs received messages with timestamps

#### Key Classes:
- `Component2Application` - Main Spring Boot application
- `ArtemisMessageListener` - JMS listener for component2.queue
- `ArtemisJmsConfig` - Artemis connection configuration

#### Queue Configuration:
- Queue Name: `component2.queue`
- Concurrent Consumers: 5

### Component 4: Secondary Consumer Service
**Port:** 8084  
**Purpose:** Consumes and processes messages from component4.queue (indirect consumption via Component 1)

#### Responsibilities:
1. **Message Consumption** - Listens to component4.queue
2. **Message Processing** - Processes business logic
3. **Logging** - Logs received messages with timestamps

#### Key Classes:
- `Component4Application` - Main Spring Boot application
- `ArtemisMessageListener` - JMS listener for component4.queue
- `ArtemisJmsConfig` - Artemis connection configuration

#### Queue Configuration:
- Queue Name: `component4.queue`
- Concurrent Consumers: 5

## Message Flow Scenarios

### Scenario 1: REST API Message Flow
1. Client sends HTTP POST to `/api/messages/send?queue=component2.queue`
2. Component 1 receives the request
3. Component 1 validates and logs the message
4. Component 1 sends message to Artemis (component2.queue)
5. Component 2 receives and processes the message
6. Both components log the complete flow

### Scenario 2: Multiple Queue Routing
1. Client sends message to Component 1
2. Component 1 forwards to both component2.queue AND component4.queue
3. Component 2 processes from component2.queue
4. Component 4 processes from component4.queue
5. Parallel message consumption

### Scenario 3: WebLogic to Artemis Migration (Optional)
1. Message arrives in WebLogic queue
2. Component 1's WebLogic listener receives the message
3. Component 1 logs the WebLogic message
4. Component 1 forwards to Artemis queues
5. Components 2 & 4 consume from Artemis
6. Demonstrates migration path

## Technology Stack

### Core Technologies
- **Java:** 11
- **Spring Boot:** 2.7.14
- **Apache ActiveMQ Artemis:** 2.28.0
- **Maven:** 3.6+
- **Docker:** Latest

### Spring Boot Starters Used
- `spring-boot-starter-web` - REST API support
- `spring-boot-starter-artemis` - Artemis JMS integration
- `spring-boot-starter-test` - Testing framework
- `spring-boot-configuration-processor` - Configuration metadata

### Additional Libraries
- **Lombok** - Reduces boilerplate code
- **Artemis JMS Client** - Artemis connectivity
- **SLF4J/Logback** - Logging framework

## Configuration Management

### Environment Variables
All components support environment-based configuration:

```bash
# Artemis Connection
ARTEMIS_BROKER_URL=tcp://artemis:61616
ARTEMIS_BROKER_USER=admin
ARTEMIS_BROKER_PASSWORD=admin

# WebLogic Connection (Component 1 only, optional)
WEBLOGIC_URL=t3://weblogic-server:7001
WEBLOGIC_USERNAME=weblogic
WEBLOGIC_PASSWORD=welcome1
```

### Spring Profiles
- `artemis-only` - Run Component 1 without WebLogic (default for POC)
- `production` - Production configuration (can be added)

## Docker Configuration

### docker-compose.yml Structure
```yaml
services:
  artemis:      # Artemis broker
  component-1:  # Adapter service
  component-2:  # Consumer service
  component-4:  # Consumer service

networks:
  artemis-network:  # Isolated network
```

### Container Dependencies
- All components depend on Artemis health check
- Services start only after Artemis is fully initialized
- Automatic restart on failure

## Queue Configuration

### Artemis Queues
1. **component2.queue**
   - Type: Anycast (point-to-point)
   - Consumers: Component 2
   - Purpose: Primary message consumption

2. **component4.queue**
   - Type: Anycast (point-to-point)
   - Consumers: Component 4
   - Purpose: Secondary/parallel message consumption

3. **weblogic.input.queue** (Optional)
   - Type: Anycast
   - Consumers: Component 1 (when WebLogic is enabled)
   - Purpose: WebLogic migration queue

### Queue Features
- Auto-create queues: Enabled
- Dead Letter Queue (DLQ): Configured
- Expiry Queue: Configured
- Persistence: Enabled
- Message redelivery: Supported

## Security Considerations

### Current Implementation
- Basic authentication (admin/admin) for Artemis
- No encryption in transit (POC level)
- Security settings configured in broker.xml

### Production Recommendations
1. Use SSL/TLS for all connections
2. Implement proper user management
3. Use environment-specific credentials
4. Enable message encryption
5. Implement audit logging
6. Use Spring Security for REST endpoints

## Scalability & Performance

### Current Configuration
- 5 concurrent consumers per component
- Connection pooling via CachingConnectionFactory
- Session transactions enabled
- Persistent message delivery

### Scaling Options
1. **Horizontal Scaling**
   - Deploy multiple instances of Components 2 & 4
   - Load balancing via Artemis
   - Message distribution across consumers

2. **Vertical Scaling**
   - Increase concurrent consumers
   - Adjust JVM heap size
   - Optimize Artemis broker settings

3. **Artemis Clustering**
   - Deploy Artemis in cluster mode
   - High availability configuration
   - Message redistribution

## Monitoring & Observability

### Current Logging
- SLF4J/Logback for all components
- DEBUG level for JMS operations
- INFO level for business logic
- Timestamp-based correlation

### Production Monitoring
1. **Metrics**
   - Message throughput
   - Consumer lag
   - Connection pool stats
   - JVM metrics

2. **Tools**
   - Spring Boot Actuator
   - Prometheus/Grafana
   - ELK Stack for logs
   - Artemis Web Console

3. **Health Checks**
   - REST health endpoints
   - Artemis broker health
   - Queue depth monitoring

## Testing Strategy

### Unit Testing
- Service layer tests
- Controller tests with MockMvc
- JMS configuration tests

### Integration Testing
- End-to-end message flow tests
- Artemis embedded mode for tests
- REST API integration tests

### Load Testing
- JMeter/Gatling for REST API
- JMS performance testing
- Concurrent consumer testing

## Deployment Options

### 1. Docker Compose (Development/POC)
- All services in containers
- Single host deployment
- Easy setup and teardown

### 2. Kubernetes (Production)
- Container orchestration
- Auto-scaling
- Service discovery
- Load balancing

### 3. Traditional Deployment
- JAR files on application servers
- External Artemis broker
- Manual configuration

## Migration Strategy from WebLogic

### Phase 1: Parallel Run
1. Enable WebLogic listener in Component 1
2. Run both WebLogic and Artemis
3. Component 1 forwards from WebLogic to Artemis
4. Validate message flow

### Phase 2: Traffic Switch
1. Route new traffic to Artemis
2. Monitor WebLogic queue drain
3. Validate business logic

### Phase 3: WebLogic Decommission
1. Stop WebLogic listeners
2. Remove WebLogic configuration
3. Pure Artemis deployment

## Best Practices Implemented

1. **Separation of Concerns** - Each component has specific responsibility
2. **Configuration Externalization** - Environment-based config
3. **Conditional Bean Creation** - WebLogic config only when needed
4. **Connection Pooling** - CachingConnectionFactory for efficiency
5. **Transaction Management** - Session-based transactions
6. **Comprehensive Logging** - Full message flow visibility
7. **Error Handling** - Try-catch with logging
8. **Health Endpoints** - Monitoring support
9. **Docker Support** - Container-ready
10. **Documentation** - Clear README and guides

## Future Enhancements

1. Message transformation and enrichment
2. Content-based routing
3. Dead letter queue handling
4. Message retry with exponential backoff
5. Distributed tracing (Zipkin/Jaeger)
6. Schema validation for XML/JSON
7. Rate limiting
8. Circuit breaker pattern
9. API Gateway integration
10. Kubernetes helm charts
