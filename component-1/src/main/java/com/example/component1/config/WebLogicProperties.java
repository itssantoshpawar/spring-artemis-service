package com.example.component1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "weblogic")
public class WebLogicProperties {
    
    private JmsConfig jms;
    
    @Data
    public static class JmsConfig {
        private String url;
        private String username;
        private String password;
        private String initialContextFactory;
        private String connectionFactory;
    }
}
