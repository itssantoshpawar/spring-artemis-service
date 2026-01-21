package com.example.component1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "artemis")
public class ArtemisProperties {
    
    private BrokerConfig broker;
    private List<ListenerConfig> listener;
    
    @Data
    public static class BrokerConfig {
        private String url;
        private String user;
        private String password;
        private boolean embedded;
    }
    
    @Data
    public static class ListenerConfig {
        private String name;
        private String brokerUrl;
        private String username;
        private String password;
        private int concurrentConsumers;
    }
}
