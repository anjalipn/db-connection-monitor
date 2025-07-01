# Database Connection Monitor

A reusable Spring Boot library for monitoring database connections and automatically shutting down applications when critical connection issues are detected.

## Features

- **Periodic Connection Monitoring**: Automatically tests database connections at configurable intervals
- **Conservative Shutdown Strategy**: Only shuts down when safe (no active transactions)
- **Pool Utilization Monitoring**: Logs warnings for critical pool utilization levels
- **Comprehensive Logging**: Detailed logging of all monitoring activities and shutdown decisions
- **Easy Integration**: Auto-configuration for seamless integration into existing Spring Boot applications

## Quick Start

### 1. Add Dependency

Add this library to your Spring Boot application's `pom.xml`:

```xml
<dependency>
    <groupId>com.apimonitor</groupId>
    <artifactId>db-connection-monitor</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Enable Monitoring

Add the following configuration to your `application.yml`:

```yaml
# Enable database monitoring
db:
  monitor:
    enabled: true
    # Health check query (default: SELECT 1)
    health-check-query: "SELECT 1"

    # Maximum consecutive failures before considering shutdown (default: 3)
    max-failure-threshold: 3
    # Critical pool utilization threshold for logging warnings (default: 0.9)
    critical-pool-utilization: 0.9
    # Monitoring interval in milliseconds (default: 30000)
    monitoring-interval: 30000
```

### 3. That's It!

The library will automatically start monitoring your database connections. No additional code required!

## Configuration Options

| Property | Default | Description |
|----------|---------|-------------|
| `db.monitor.enabled` | `true` | Enable/disable the database monitor |
| `db.monitor.health-check-query` | `SELECT 1` | SQL query to test database connectivity |
| `db.monitor.max-failure-threshold` | `3` | Maximum consecutive failures before considering shutdown |
| `db.monitor.critical-pool-utilization` | `0.9` | Critical pool utilization threshold (0.0-1.0) for logging warnings |
| `db.monitor.monitoring-interval` | `30000` | Monitoring interval in milliseconds |



## Shutdown Strategy

The application uses a **conservative shutdown strategy** that prioritizes data integrity:

### When Shutdown is Considered
The application will consider shutdown when:
1. **Connection Test Fails**: A new database connection cannot be obtained
2. **Failure Threshold Reached**: The number of consecutive failures reaches the configured threshold

### When Shutdown Actually Occurs
**Shutdown only happens when it's safe to do so:**
- ✅ **No Active Connections**: When `activeConnections == 0` (no active transactions)
- ❌ **Active Connections Present**: If there are active connections, shutdown is deferred until the next monitoring cycle

This ensures that:
- **No active transactions are killed**
- **No active threads are terminated immediately**
- **Data integrity is preserved**
- **The application exits with a non-zero code for Kubernetes to restart the pod**

## Logging

The library provides comprehensive logging at different levels:

- **DEBUG**: Detailed monitoring cycle information and pool utilization metrics
- **INFO**: Safe shutdown checks and pool state information
- **WARN**: Critical pool utilization warnings and connection failures
- **ERROR**: Shutdown decisions and final pool state

Example log output:
```
2024-01-15 10:30:00 [scheduling-1] DEBUG c.a.d.s.DatabaseConnectionMonitorService - Pool utilization check - Max: 10, Active: 3, Idle: 7, Total: 10, Waiting: 0, Utilization: 30.0%
2024-01-15 10:30:30 [scheduling-1] WARN  c.a.d.s.DatabaseConnectionMonitorService - Critical pool utilization detected: 95.0% (threshold: 90.0%) - Active: 9, Max: 10, Waiting: 2
2024-01-15 10:31:00 [scheduling-1] WARN  c.a.d.s.DatabaseConnectionMonitorService - Database connection test failed. Consecutive failures: 1
2024-01-15 10:31:30 [scheduling-1] INFO  c.a.d.s.DatabaseConnectionMonitorService - Safe shutdown check - Active: 0, Idle: 0, Total: 0, Waiting: 0
2024-01-15 10:31:30 [scheduling-1] ERROR c.a.d.s.DatabaseConnectionMonitorService - Safe to shutdown: No active connections detected. Reason: Connection test failed repeatedly. Initiating application shutdown.
```

## Building the Library

To build the library:

```bash
mvn clean package
```

This will create a JAR file that can be included in other Spring Boot applications.

## Example Integration

Here's a complete example of how to integrate this library into a Spring Boot application:

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: myuser
    password: mypassword
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5

# Database monitoring configuration
db:
  monitor:
    enabled: true
    health-check-query: "SELECT 1 FROM dual"
    max-failure-threshold: 5
    critical-pool-utilization: 0.85  # Log warnings at 85% utilization
    monitoring-interval: 60000  # Check every minute
```

### pom.xml
```xml
<dependency>
    <groupId>com.apimonitor</groupId>
    <artifactId>db-connection-monitor</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Requirements

- Java 17 or higher
- Spring Boot 3.2.0 or higher
- HikariCP connection pool (automatically included with Spring Boot)
