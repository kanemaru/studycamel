package jp.biglobe.command;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

public class CommandReceiver {

    public static void main(String[] args) {

        try{
            //Connectionを作成するFactoryを作成
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            QueueConnection connection = factory.createQueueConnection();

            //セッションの作成
            QueueSession session = connection.createQueueSession(false,QueueSession.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("BchatMessageQueue");

            //Queueと関連付け
            QueueReceiver receiver = session.createReceiver(queue);

            connection.start();

            while (true) {
                //メッセージの受信
                ObjectMessage msg = (ObjectMessage) receiver.receive();
                System.out.println(msg.getObject());
            }

//            receiver.close();
//            session.close();
//            connection.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
