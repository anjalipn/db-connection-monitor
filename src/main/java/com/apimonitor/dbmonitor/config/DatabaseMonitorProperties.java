package com.apimonitor.dbmonitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Database Connection Monitor.
 * Maps configuration properties from application.yml/application.properties
 * to Java objects for type-safe configuration.
 */
@Data
@ConfigurationProperties(prefix = "db.monitor")
public class DatabaseMonitorProperties {

    /**
     * Whether the database monitor is enabled.
     */
    private boolean enabled = true;

    /**
     * Health check query to execute for connection testing.
     */
    private String healthCheckQuery = "SELECT 1";

    /**
     * Connection timeout in milliseconds.
     */
    private int connectionTimeout = 5000;

    /**
     * Maximum consecutive failures before shutdown.
     */
    private int maxFailureThreshold = 3;



    /**
     * Monitoring interval in milliseconds.
     */
    private long monitoringInterval = 30000;
} 