package com.example.component1.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

@Configuration
@ConditionalOnProperty(prefix = "weblogic.jms", name = "url")
public class WebLogicJmsConfig {

    private final WebLogicProperties webLogicProperties;

    public WebLogicJmsConfig(WebLogicProperties webLogicProperties) {
        this.webLogicProperties = webLogicProperties;
    }

    @Bean(name = "weblogicConnectionFactory")
    public ConnectionFactory weblogicConnectionFactory() throws NamingException {
        WebLogicProperties.JmsConfig jms = webLogicProperties.getJms();
        
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, jms.getInitialContextFactory());
        props.put(Context.PROVIDER_URL, jms.getUrl());
        props.put(Context.SECURITY_PRINCIPAL, jms.getUsername());
        props.put(Context.SECURITY_CREDENTIALS, jms.getPassword());
        
        InitialContext ctx = new InitialContext(props);
        ConnectionFactory factory = (ConnectionFactory) ctx.lookup(jms.getConnectionFactory());
        
        return new CachingConnectionFactory(factory);
    }

    @Bean(name = "weblogicJmsTemplate")
    public JmsTemplate weblogicJmsTemplate(@Qualifier("weblogicConnectionFactory") ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setDeliveryPersistent(true);
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    @Bean(name = "weblogicListenerContainerFactory")
    public DefaultJmsListenerContainerFactory weblogicListenerContainerFactory(
            @Qualifier("weblogicConnectionFactory") ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("3-10");
        factory.setSessionTransacted(true);
        return factory;
    }
}
