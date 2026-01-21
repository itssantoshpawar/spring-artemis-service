package com.example.component3.controller;

import com.example.component3.service.MessageForwardingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final MessageForwardingService messageForwardingService;

    public MessageController(MessageForwardingService messageForwardingService) {
        this.messageForwardingService = messageForwardingService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody String message) {
        try {
            logger.info("Received request to send message: {}", message);
            messageForwardingService.forwardToComponent4(message);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Message sent to Component 4 queue successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error sending message: {}", e.getMessage(), e);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("component", "Component 3 - Producer/Consumer Service");
        return ResponseEntity.ok(response);
    }
}
