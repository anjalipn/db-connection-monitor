package com.example.helloworld.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Message Entity
 * 
 * A simple entity to demonstrate database operations in the Hello World API.
 */
@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "user_name")
    private String userName;

    /**
     * Constructor for creating a new message
     */
    public Message(String content, String userName) {
        this.content = content;
        this.userName = userName;
        this.createdAt = LocalDateTime.now();
    }
} 