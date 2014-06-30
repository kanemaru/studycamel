package jp.biglobe.bchat.mqueue;

import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class MessageQueueListener implements MessageListener {

    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                System.out.println("receive: " + ((TextMessage) message).getText());

            }
            catch (JMSException ex) {
                throw new RuntimeException(ex);
            }
        }
        else {
            System.out.println("receive: " + message);
        }
    }
}
