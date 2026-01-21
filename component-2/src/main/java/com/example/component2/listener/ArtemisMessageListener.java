package com.example.component2.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ArtemisMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(ArtemisMessageListener.class);

    @JmsListener(destination = "${artemis.queue.name:component2.queue}")
    public void receiveMessage(String message) {
        logger.info("============================================");
        logger.info("COMPONENT 2: Received message from Artemis");
        logger.info("Queue: component2.queue");
        logger.info("Message: {}", message);
        logger.info("Timestamp: {}", System.currentTimeMillis());
        logger.info("============================================");
        
        // Process the message
        processMessage(message);
    }

    private void processMessage(String message) {
        logger.info("Processing message in Component 2...");
        
        // Add your business logic here
        // For POC, we just log the message
        
        logger.info("Message processed successfully");
    }
}
