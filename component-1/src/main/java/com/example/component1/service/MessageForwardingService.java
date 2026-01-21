package com.example.component1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageForwardingService {

    private static final Logger logger = LoggerFactory.getLogger(MessageForwardingService.class);

    private final JmsTemplate artemisJmsTemplate;

    public MessageForwardingService(@Qualifier("artemisJmsTemplate") JmsTemplate artemisJmsTemplate) {
        this.artemisJmsTemplate = artemisJmsTemplate;
    }

    /**
     * Forward message from WebLogic to Artemis
     */
    public void forwardToArtemis(String destination, String message) {
        logger.info("Forwarding message to Artemis queue: {}", destination);
        logger.info("Message content: {}", message);
        
        try {
            artemisJmsTemplate.convertAndSend(destination, message);
            logger.info("Message successfully forwarded to Artemis queue: {}", destination);
        } catch (Exception e) {
            logger.error("Error forwarding message to Artemis: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to forward message to Artemis", e);
        }
    }

    /**
     * Send message directly to Artemis (for REST API)
     */
    public void sendToArtemis(String destination, String message) {
        logger.info("Sending message to Artemis queue: {}", destination);
        logger.info("Message content: {}", message);
        
        try {
            artemisJmsTemplate.convertAndSend(destination, message);
            logger.info("Message successfully sent to Artemis queue: {}", destination);
        } catch (Exception e) {
            logger.error("Error sending message to Artemis: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send message to Artemis", e);
        }
    }
}
