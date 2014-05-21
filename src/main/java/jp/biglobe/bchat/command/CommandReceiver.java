package jp.biglobe.bchat.command;

import jp.biglobe.bchat.queue.QueueHandler;

public class CommandReceiver {

    private static QueueHandler queueHandler;

    public static void main(String[] args) {

        try {

            queueHandler = new QueueHandler();

            while (true) {
                //メッセージの受信
                Object obj = queueHandler.receiveObject();
                System.out.println(obj);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (queueHandler != null) {
            queueHandler.closeAllResources();
        }

    }
}
