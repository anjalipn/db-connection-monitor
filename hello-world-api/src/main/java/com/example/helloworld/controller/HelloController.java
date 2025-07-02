package com.example.helloworld.controller;

import com.example.helloworld.entity.Message;
import com.example.helloworld.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hello World REST Controller
 * 
 * Provides simple REST endpoints to demonstrate the API functionality.
 * The database connection monitor library runs in the background
 * monitoring database connectivity.
 */
@RestController
@RequestMapping("/api/hello")
@Slf4j
@RequiredArgsConstructor
public class HelloController {

    private final MessageService messageService;

    /**
     * Simple hello endpoint
     */
    @GetMapping
    public Map<String, Object> hello() {
        log.info("Hello endpoint called");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello, World!");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "success");
        
        return response;
    }

    /**
     * Personalized hello endpoint
     */
    @GetMapping("/greet")
    public Map<String, Object> greet(@RequestParam(defaultValue = "User") String name) {
        log.info("Greet endpoint called with name: {}", name);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello, " + name + "!");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "success");
        
        return response;
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        log.info("Health check endpoint called");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Hello World API");
        response.put("database-monitor", "enabled");
        
        return response;
    }

    /**
     * Create a new message
     */
    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestParam String content, 
                                                @RequestParam String userName) {
        log.info("Creating message for user: {}", userName);
        Message message = messageService.createMessage(content, userName);
        return ResponseEntity.ok(message);
    }

    /**
     * Get all messages
     */
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        log.info("Retrieving all messages");
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    /**
     * Get recent messages
     */
    @GetMapping("/messages/recent")
    public ResponseEntity<List<Message>> getRecentMessages() {
        log.info("Retrieving recent messages");
        List<Message> messages = messageService.getRecentMessages();
        return ResponseEntity.ok(messages);
    }

    /**
     * Get messages by user
     */
    @GetMapping("/messages/user/{userName}")
    public ResponseEntity<List<Message>> getMessagesByUser(@PathVariable String userName) {
        log.info("Retrieving messages for user: {}", userName);
        List<Message> messages = messageService.getMessagesByUser(userName);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get message by ID
     */
    @GetMapping("/messages/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        log.info("Retrieving message with ID: {}", id);
        Message message = messageService.getMessageById(id);
        return ResponseEntity.ok(message);
    }
} 