package com.example.component1.config;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

@Configuration
public class ArtemisJmsConfig {

    private final ArtemisProperties artemisProperties;

    public ArtemisJmsConfig(ArtemisProperties artemisProperties) {
        this.artemisProperties = artemisProperties;
    }

    @Primary
    @Bean(name = "artemisConnectionFactory")
    public ConnectionFactory artemisConnectionFactory() {
        ArtemisProperties.BrokerConfig broker = artemisProperties.getBroker();
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
                broker.getUrl(),
                broker.getUser(),
                broker.getPassword()
        );
        return new CachingConnectionFactory(factory);
    }

    @Primary
    @Bean(name = "artemisJmsTemplate")
    public JmsTemplate artemisJmsTemplate(@Qualifier("artemisConnectionFactory") ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setDeliveryPersistent(true);
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    @Bean(name = "artemisListenerContainerFactory")
    public DefaultJmsListenerContainerFactory artemisListenerContainerFactory(
            @Qualifier("artemisConnectionFactory") ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        
        if (artemisProperties.getListener() != null && !artemisProperties.getListener().isEmpty()) {
            ArtemisProperties.ListenerConfig listenerConfig = artemisProperties.getListener().get(0);
            factory.setConcurrency(String.valueOf(listenerConfig.getConcurrentConsumers()));
        }
        
        factory.setSessionTransacted(true);
        return factory;
    }
}
