package com.apimonitor.dbmonitor.service;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Service responsible for monitoring database connections and making
 * informed decisions about application termination based on connection pool health.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "db.monitor.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseConnectionMonitorService {

    @Autowired
    private DataSource dataSource;

    @Value("${db.monitor.health-check-query:SELECT 1}")
    private String healthCheckQuery;

    @Value("${db.monitor.max-failure-threshold:3}")
    private int maxFailureThreshold;

    @Value("${db.monitor.critical-pool-utilization:0.9}")
    private double criticalPoolUtilization;



    private int consecutiveFailures = 0;
    private boolean isShutdownInProgress = false;

    public DatabaseConnectionMonitorService() {
        log.warn("DatabaseConnectionMonitorService instantiated");
    }

    /**
     * Scheduled method that runs periodically to monitor database connections.
     * Default interval is 30 seconds, configurable via db.monitor.monitoring-interval
     */
    @Scheduled(fixedDelayString = "${db.monitor.monitoring-interval:30000}")
    public void monitorDatabaseConnection() {
        log.warn("Scheduled monitor task triggered");
        if (isShutdownInProgress) {
            log.warn("Shutdown already in progress, skipping monitoring cycle");
            return;
        }

        log.debug("Starting database connection monitoring cycle");

        try {
            // Test database connection
            boolean connectionSuccessful = testDatabaseConnection();
            
            if (connectionSuccessful) {
                consecutiveFailures = 0;
                log.debug("Database connection test successful");
            } else {
                consecutiveFailures++;
                log.warn("Database connection test failed. Consecutive failures: {}", consecutiveFailures);
                
                // Only consider shutdown if we've reached the failure threshold
                if (consecutiveFailures >= maxFailureThreshold) {
                    checkForSafeShutdown("Connection test failed repeatedly");
                }
            }

            // Always check pool utilization for logging purposes
            checkPoolUtilization();

        } catch (Exception e) {
            log.error("Error during database monitoring cycle", e);
            consecutiveFailures++;
            
            if (consecutiveFailures >= maxFailureThreshold) {
                checkForSafeShutdown("Monitoring service errors");
            }
        }
    }

    /**
     * Tests the database connection by executing a simple query.
     * 
     * @return true if connection is successful, false otherwise
     */
    private boolean testDatabaseConnection() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(healthCheckQuery);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                log.debug("Health check query executed successfully: {}", healthCheckQuery);
                return true;
            } else {
                log.warn("Health check query returned no results");
                return false;
            }
        } catch (SQLException e) {
            log.error("Database connection test failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks pool utilization and logs warnings for critical levels.
     * This is for monitoring purposes only - no shutdown actions are taken.
     */
    private void checkPoolUtilization() {
        if (!(dataSource instanceof HikariDataSource)) {
            return;
        }

        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        HikariPoolMXBean poolMXBean = hikariDataSource.getHikariPoolMXBean();

        if (poolMXBean == null) {
            return;
        }

        int maxPoolSize = hikariDataSource.getMaximumPoolSize();
        int activeConnections = poolMXBean.getActiveConnections();
        int idleConnections = poolMXBean.getIdleConnections();
        int totalConnections = poolMXBean.getTotalConnections();
        int threadsAwaitingConnection = poolMXBean.getThreadsAwaitingConnection();

        // Calculate pool utilization
        double poolUtilization = maxPoolSize > 0 ? (double) activeConnections / maxPoolSize : 0.0;

        log.debug("Pool utilization check - Max: {}, Active: {}, Idle: {}, Total: {}, Waiting: {}, Utilization: {}%", 
                   maxPoolSize, activeConnections, idleConnections, totalConnections, threadsAwaitingConnection,
                   String.format("%.1f", poolUtilization * 100));

        // Log warning for critical pool utilization
        if (poolUtilization >= criticalPoolUtilization) {
            log.warn("Critical pool utilization detected: {}% (threshold: {}%) - Active: {}, Max: {}, Waiting: {}", 
                       String.format("%.1f", poolUtilization * 100), 
                       String.format("%.1f", criticalPoolUtilization * 100),
                       activeConnections, maxPoolSize, threadsAwaitingConnection);
        }
    }

    /**
     * Checks if it's safe to shutdown the application based on connection pool state.
     * Only shuts down if there are no active connections (no active transactions).
     * 
     * @param reason The reason for considering shutdown
     */
    private void checkForSafeShutdown(String reason) {
        if (!(dataSource instanceof HikariDataSource)) {
            log.warn("DataSource is not HikariCP, cannot check pool state for safe shutdown");
            return;
        }

        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        HikariPoolMXBean poolMXBean = hikariDataSource.getHikariPoolMXBean();

        if (poolMXBean == null) {
            log.warn("HikariPoolMXBean is not available for safe shutdown check");
            return;
        }

        int activeConnections = poolMXBean.getActiveConnections();
        int idleConnections = poolMXBean.getIdleConnections();
        int totalConnections = poolMXBean.getTotalConnections();
        int threadsAwaitingConnection = poolMXBean.getThreadsAwaitingConnection();

        log.info("Safe shutdown check - Active: {}, Idle: {}, Total: {}, Waiting: {}", 
                  activeConnections, idleConnections, totalConnections, threadsAwaitingConnection);

        // Only shutdown if there are NO active connections (no active transactions)
        if (activeConnections == 0) {
            log.error("Safe to shutdown: No active connections detected. Reason: {}. Initiating application shutdown.", reason);
            initiateApplicationShutdown(reason + " - No active transactions");
        } else {
            log.warn("Cannot shutdown safely: {} active connections detected. Will retry on next monitoring cycle.", activeConnections);
        }
    }

    /**
     * Initiates application shutdown.
     * 
     * @param reason The reason for shutdown
     */
    private void initiateApplicationShutdown(String reason) {
        if (isShutdownInProgress) {
            return;
        }

        isShutdownInProgress = true;
        log.error("Database connection monitor: Shutting down application. Reason: {}", reason);
        
        // Log current pool state before shutdown
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            HikariPoolMXBean poolMXBean = hikariDataSource.getHikariPoolMXBean();
            if (poolMXBean != null) {
                log.error("Final pool state - Active: {}, Idle: {}, Total: {}, Waiting: {}", 
                           poolMXBean.getActiveConnections(), 
                           poolMXBean.getIdleConnections(),
                           poolMXBean.getTotalConnections(),
                           poolMXBean.getThreadsAwaitingConnection());
            }
        }
        System.exit(1);
    }


} 