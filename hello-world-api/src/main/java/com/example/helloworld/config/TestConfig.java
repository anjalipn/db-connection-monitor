package com.example.helloworld.config;

import com.apimonitor.dbmonitor.service.DatabaseConnectionMonitorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class TestConfig {

    @Bean
    public DatabaseConnectionMonitorService databaseConnectionMonitorService() {
        System.out.println("MANUAL BEAN CREATION: DatabaseConnectionMonitorService");
        return new DatabaseConnectionMonitorService();
    }
} 