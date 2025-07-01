package com.apimonitor.dbmonitor.config;

import com.apimonitor.dbmonitor.service.DatabaseConnectionMonitorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Auto-configuration for the Database Connection Monitor.
 * This class enables automatic configuration when the library is included
 * in a Spring Boot application's classpath.
 */
@Configuration
@ConditionalOnClass(DatabaseConnectionMonitorService.class)
@ConditionalOnProperty(name = "db.monitor.enabled", havingValue = "true", matchIfMissing = true)

@EnableScheduling
public class DatabaseMonitorAutoConfiguration {
} 