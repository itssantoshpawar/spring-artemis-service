package com.example.component1.controller;

import com.example.component1.service.MessageForwardingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final MessageForwardingService forwardingService;

    public MessageController(MessageForwardingService forwardingService) {
        this.forwardingService = forwardingService;
    }

    @PostMapping(value = "/send", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestParam(name = "queue", defaultValue = "component2.queue") String queueName,
            @RequestBody String message) {
        
        logger.info("Received REST request to send message to queue: {}", queueName);
        logger.info("Message payload: {}", message);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            forwardingService.sendToArtemis(queueName, message);
            
            response.put("status", "success");
            response.put("message", "Message sent successfully");
            response.put("queue", queueName);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error sending message: {}", e.getMessage(), e);
            
            response.put("status", "error");
            response.put("message", "Failed to send message: " + e.getMessage());
            response.put("queue", queueName);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("component", "Component-1");
        response.put("description", "WebLogic to Artemis Adapter");
        return ResponseEntity.ok(response);
    }
}
