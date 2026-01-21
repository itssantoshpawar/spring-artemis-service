package com.example.component3.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageForwardingService {

    private static final Logger logger = LoggerFactory.getLogger(MessageForwardingService.class);
    private final JmsTemplate jmsTemplate;

    @Value("${artemis.queue.output:component4.queue}")
    private String outputQueue;

    public MessageForwardingService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void forwardToComponent4(String message) {
        try {
            logger.info("Sending message to queue: {}", outputQueue);
            jmsTemplate.convertAndSend(outputQueue, message);
            logger.info("Message sent successfully to Component 4 queue");
        } catch (Exception e) {
            logger.error("Error sending message to Component 4: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to forward message to Component 4", e);
        }
    }

    public void sendMessage(String message) {
        forwardToComponent4(message);
    }
}
