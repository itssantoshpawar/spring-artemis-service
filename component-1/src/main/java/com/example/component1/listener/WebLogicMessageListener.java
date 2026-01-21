package com.example.component1.listener;

import com.example.component1.service.MessageForwardingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "weblogic.jms", name = "url", matchIfMissing = false)
public class WebLogicMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(WebLogicMessageListener.class);

    private final MessageForwardingService forwardingService;

    public WebLogicMessageListener(MessageForwardingService forwardingService) {
        this.forwardingService = forwardingService;
    }

    @JmsListener(destination = "${weblogic.queue.input:weblogic.input.queue}", 
                 containerFactory = "weblogicListenerContainerFactory")
    public void receiveMessage(String message) {
        logger.info("============================================");
        logger.info("Received message from WebLogic queue");
        logger.info("Message: {}", message);
        logger.info("============================================");
        
        try {
            // Forward to Artemis
            String artemisQueue = "component2.queue";
            forwardingService.forwardToArtemis(artemisQueue, message);
            
            // Also forward to Component 4 queue
            String component4Queue = "component4.queue";
            forwardingService.forwardToArtemis(component4Queue, message);
            
            logger.info("Message successfully processed and forwarded");
        } catch (Exception e) {
            logger.error("Error processing message from WebLogic: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process WebLogic message", e);
        }
    }
}
