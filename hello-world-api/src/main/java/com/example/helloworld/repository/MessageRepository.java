package com.example.helloworld.repository;

import com.example.helloworld.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Message Repository
 * 
 * Provides data access methods for the Message entity.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find messages by user name
     */
    List<Message> findByUserNameOrderByCreatedAtDesc(String userName);

    /**
     * Find recent messages (last 10)
     */
    List<Message> findTop10ByOrderByCreatedAtDesc();
} 