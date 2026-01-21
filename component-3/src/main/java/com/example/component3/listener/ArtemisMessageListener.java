package com.example.component3.listener;

import com.example.component3.service.MessageForwardingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ArtemisMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(ArtemisMessageListener.class);
    private final MessageForwardingService messageForwardingService;

    public ArtemisMessageListener(MessageForwardingService messageForwardingService) {
        this.messageForwardingService = messageForwardingService;
    }

    @JmsListener(destination = "${artemis.queue.input:component3-to-component4}")
    public void receiveMessage(String message) {
        logger.info("============================================");
        logger.info("COMPONENT 3: Received message from Artemis");
        logger.info("Queue: component3-to-component4");
        logger.info("Message: {}", message);
        logger.info("Timestamp: {}", System.currentTimeMillis());
        logger.info("============================================");
        
        // Process and forward the message to Component 4
        processAndForwardMessage(message);
    }

    private void processAndForwardMessage(String message) {
        logger.info("Processing message in Component 3...");
        
        // Add any business logic here
        String processedMessage = "Processed by Component 3: " + message;
        
        // Forward to Component 4
        logger.info("Forwarding message to Component 4 queue...");
        messageForwardingService.forwardToComponent4(processedMessage);
        
        logger.info("Message processed and forwarded successfully");
    }
}
