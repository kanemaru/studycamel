package jp.biglobe.bchat.config;

import jp.biglobe.bchat.websocket.handler.ChatHandler;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class ApplicationConfig implements WebSocketConfigurer {

//    @Autowired
//    JmsTemplate jmsTemplate;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        ActiveMQConnectionFactory connectionFactoryAMQ = new ActiveMQConnectionFactory("tcp://localhost:61616");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(connectionFactoryAMQ);
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setDefaultDestinationName("BChatMessage");

        registry.addHandler(new ChatHandler(jmsTemplate), "/bchat");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}