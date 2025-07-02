# Hello World API

A simple Spring Boot REST API that demonstrates the usage of the database connection monitor library.

## Features

- **Hello World Endpoints**: Basic REST endpoints for greeting users
- **Message Management**: CRUD operations for messages with database persistence
- **Database Monitoring**: Automatic database connection monitoring using the `db-connection-monitor` library
- **Health Checks**: Built-in health check endpoints
- **H2 Database**: In-memory database for demonstration purposes

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Getting Started

### 1. Build the Project

```bash
mvn clean install
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 3. Access the API

#### Hello World Endpoints

- **GET** `/api/hello` - Simple hello message
- **GET** `/api/hello/greet?name=YourName` - Personalized greeting
- **GET** `/api/hello/health` - Health check

#### Message Endpoints

- **POST** `/api/hello/messages?content=Hello&userName=John` - Create a new message
- **GET** `/api/hello/messages` - Get all messages
- **GET** `/api/hello/messages/recent` - Get recent messages (last 10)
- **GET** `/api/hello/messages/user/{userName}` - Get messages by user
- **GET** `/api/hello/messages/{id}` - Get message by ID

### 4. Database Console

Access the H2 database console at: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## Database Connection Monitoring

This application uses the `db-connection-monitor` library which provides:

- **Automatic Health Checks**: Periodically tests database connectivity
- **Connection Pool Monitoring**: Monitors HikariCP connection pool metrics
- **Critical Pool Utilization Logging**: Logs warnings when pool utilization is high
- **Graceful Shutdown**: Shuts down the application if database becomes unreachable and no active connections exist

### Configuration

The monitoring is configured in `application.yml`:

```yaml
db:
  monitor:
    enabled: true
    health-check-query: "SELECT 1"
    max-failure-threshold: 3
    critical-pool-utilization: 0.9
    monitoring-interval: 30000
```

## Project Structure

```
src/main/java/com/example/helloworld/
├── HelloWorldApplication.java      # Main application class
├── controller/
│   └── HelloController.java        # REST controller
├── entity/
│   └── Message.java               # JPA entity
├── repository/
│   └── MessageRepository.java     # Data access layer
└── service/
    └── MessageService.java        # Business logic
```

## Testing the API

### Using curl

```bash
# Hello endpoints
curl http://localhost:8080/api/hello
curl "http://localhost:8080/api/hello/greet?name=Alice"
curl http://localhost:8080/api/hello/health

# Message endpoints
curl -X POST "http://localhost:8080/api/hello/messages?content=Hello%20World&userName=John"
curl http://localhost:8080/api/hello/messages
curl http://localhost:8080/api/hello/messages/recent
curl http://localhost:8080/api/hello/messages/user/John
curl http://localhost:8080/api/hello/messages/1
```

### Using a REST Client

You can use tools like Postman, Insomnia, or any REST client to test the endpoints.

## Logging

The application is configured with appropriate logging levels:

- HikariCP: WARN (to reduce verbose connection logs)
- Application: INFO
- Database Monitor: INFO

## Dependencies

- **Spring Boot 3.2.0**: Core framework
- **Spring Data JPA**: Database access
- **H2 Database**: In-memory database
- **db-connection-monitor**: Database monitoring library
- **Lombok**: Reduces boilerplate code

## Development

### Adding New Features

1. Create entities in the `entity` package
2. Add repositories in the `repository` package
3. Implement business logic in the `service` package
4. Add REST endpoints in the `controller` package

### Database Changes

The application uses H2 with `create-drop` mode, so the database schema is automatically created on startup and dropped on shutdown.

## Production Considerations

For production deployment:

1. Replace H2 with a production database (PostgreSQL, MySQL, etc.)
2. Configure proper connection pooling settings
3. Set up monitoring and alerting
4. Configure appropriate logging levels
5. Set up health checks and readiness probes for Kubernetes

## License

This project is for demonstration purposes. 