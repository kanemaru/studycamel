package jp.biglobe.bchat.config;

import jp.biglobe.bchat.handler.ChatHandler;
import jp.biglobe.bchat.queue.QueueHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class ApplicationConfig implements WebSocketConfigurer {

    private static QueueHandler queueHandler = null;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(new ChatHandler(), "/bchat");

        if (queueHandler == null) {
            queueHandler = new QueueHandler();
        }
    }

    public static QueueHandler getQueueHandler() throws Exception {
        return queueHandler;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (queueHandler != null) {
            queueHandler.closeAllResources();
        }
    }
}