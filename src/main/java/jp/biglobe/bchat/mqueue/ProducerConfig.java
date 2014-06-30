package jp.biglobe.bchat.mqueue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;

@Configuration
@Component
public class ProducerConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactoryAMQ = new ActiveMQConnectionFactory("tcp://localhost:61616?wireFormat=openwire&wireFormat.tightEncodingEnabled=true");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(connectionFactoryAMQ);
        return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(){
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        return jmsTemplate;
    }
}