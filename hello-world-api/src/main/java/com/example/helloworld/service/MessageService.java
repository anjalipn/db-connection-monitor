package com.example.helloworld.service;

import com.example.helloworld.entity.Message;
import com.example.helloworld.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Message Service
 * 
 * Handles business logic for message operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;

    /**
     * Create a new message
     */
    public Message createMessage(String content, String userName) {
        log.info("Creating message for user: {}", userName);
        Message message = new Message(content, userName);
        return messageRepository.save(message);
    }

    /**
     * Get all messages
     */
    public List<Message> getAllMessages() {
        log.info("Retrieving all messages");
        return messageRepository.findAll();
    }

    /**
     * Get recent messages (last 10)
     */
    public List<Message> getRecentMessages() {
        log.info("Retrieving recent messages");
        return messageRepository.findTop10ByOrderByCreatedAtDesc();
    }

    /**
     * Get messages by user name
     */
    public List<Message> getMessagesByUser(String userName) {
        log.info("Retrieving messages for user: {}", userName);
        return messageRepository.findByUserNameOrderByCreatedAtDesc(userName);
    }

    /**
     * Get message by ID
     */
    public Message getMessageById(Long id) {
        log.info("Retrieving message with ID: {}", id);
        return messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with ID: " + id));
    }
} 