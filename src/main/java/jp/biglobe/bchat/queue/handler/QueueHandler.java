package jp.biglobe.bchat.queue.handler;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.Serializable;

/**
 * Created by sane on 2014/05/21.
 */
public class QueueHandler {

    private static String AQM_DEFAULT_URL = "tcp://localhost:61616";
    private static String AQM_QUEUE_NAME = "BCHATMessageQueue";

    private QueueConnection queueConnection = null;
    private QueueSession queueSession = null;
    private QueueSender queueSender = null;
    private QueueReceiver queueReceiver = null;

    public QueueHandler() {

        try {
            QueueConnectionFactory factory = new ActiveMQConnectionFactory(AQM_DEFAULT_URL);
            queueConnection = factory.createQueueConnection();

            queueSession = queueConnection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
            Queue queue = queueSession.createQueue(AQM_QUEUE_NAME);
            queueSender = queueSession.createSender(queue);
            queueReceiver = queueSession.createReceiver(queue);

            queueConnection.start();

        } catch (Exception e) {
            e.printStackTrace();
            closeAllResources();
        }
    }

    /**
     * オブジェクトを送る
     * @param obj 送るオブジェクト
     * @throws Exception
     */
    public void sendObject(Serializable obj) throws Exception {
        //メッセージの送信
        if (queueSession == null || queueSender == null) {
            return;
        }
        Message msg = queueSession.createObjectMessage(obj);
        queueSender.send(msg);
    }

    /**
     * オブジェクトを受信する
     * @return 受信したオブジェクト
     * @throws Exception
     */
    public Serializable receiveObject() throws Exception {
        //メッセージの受信
        if (queueReceiver == null) {
            return null;
        }
        ObjectMessage msg = (ObjectMessage) queueReceiver.receive();
        return msg.getObject();
    }

    public void closeAllResources() {

        try {
            if (queueSender != null) {
                queueSender.close();
                queueSender = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (queueReceiver != null) {
                queueReceiver.close();
                queueReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (queueSession != null) {
                queueSession.close();
                queueSession = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (queueConnection != null) {
                queueConnection.close();
                queueConnection = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
