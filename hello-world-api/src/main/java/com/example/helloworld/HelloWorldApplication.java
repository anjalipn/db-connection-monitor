package com.example.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hello World API Application
 * 
 * This application demonstrates the usage of the database connection monitor library.
 * The library will automatically start monitoring the database connections when this
 * application starts up.
 */
@SpringBootApplication
@EnableScheduling
public class HelloWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }
} 