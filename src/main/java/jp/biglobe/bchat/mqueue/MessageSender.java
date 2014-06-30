package jp.biglobe.bchat.mqueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

/**
 * Created by sane on 2014/06/27.
 */
public class MessageSender {

    JmsTemplate jmsTemplate;

    public MessageSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(Object obj) {
        jmsTemplate.convertAndSend(obj);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        jmsTemplate = null;
    }
}
