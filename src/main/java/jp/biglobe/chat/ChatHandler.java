package jp.biglobe.chat;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.jms.Queue;
import javax.jms.*;
import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Map.Entry;

public class ChatHandler extends TextWebSocketHandler {

    public static String AQM_DEFAULT_URL = "tcp://localhost:61616";
    public static String AQM_QUEUE_NAME = "BchatMessageQueue";

    private static String STATUS_INPUTTING = "inputting";
    private static String STATUS_COMMIT = "commit";

    /** セッション一覧 */
    private Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap();

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private static int messageId = 0;

    private List<ChatData> chatDataList = Collections.synchronizedList(new ArrayList<ChatData>());
    private static int MESSAGE_MAX = 100;

    private static QueueConnection queueConnection = null;
    private static QueueSession queueSession = null;
    private static QueueSender queueSender = null;
    private static boolean initFlag = false;

    public static void init() throws Exception {

        if (initFlag) {
            return;
        }

        QueueConnectionFactory factory = new ActiveMQConnectionFactory(AQM_DEFAULT_URL);
        queueConnection = factory.createQueueConnection();

        queueSession = queueConnection.createQueueSession(false,QueueSession.AUTO_ACKNOWLEDGE);
        Queue queue = queueSession.createQueue(AQM_QUEUE_NAME);
        queueSender = queueSession.createSender(queue);

        queueConnection.start();

        initFlag = true;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.sessionMap.put(session.getId(), session);

        for (ChatData chatData : chatDataList) {
            TextMessage sendMsg = new TextMessage(buildJsonMessage(STATUS_COMMIT, chatData));
            session.sendMessage(sendMsg);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus status) throws Exception {
        this.sessionMap.remove(session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        try {

            String jsonStr = message.getPayload();
            Map<String, String> jsonMap = parseJson(jsonStr);
            ChatData chatData = buildJsonToChatData(session.getId(), jsonMap);

            String status = jsonMap.get("status");

            if (STATUS_COMMIT.equals(status)) {
                messageId++;
                chatDataList.add(chatData);
                if (chatDataList.size() > MESSAGE_MAX) {
                    chatDataList.remove(0);
                }

                // MQ add
                Message msg = queueSession.createObjectMessage(chatData);
                queueSender.send(msg);
            }

            TextMessage sendMsg = new TextMessage(buildJsonMessage(status, chatData));
            for (Entry<String, WebSocketSession> entry : this.sessionMap.entrySet()) {
                entry.getValue().sendMessage(sendMsg);
            }

        } catch (Exception e) {
            // message処理中に落ちないように
            e.printStackTrace();
        }
    }

    private ChatData createChatData(String messageId, String sessionId, String name, String usericon, String message, Date date) {

        return new ChatData(
                messageId,
                sessionId,
                name,
                usericon,
                message,
                date
        );
    }

    private Map<String, String> parseJson(String jsonStr) {

        JsonParser parser =
                Json.createParser(new ByteArrayInputStream(jsonStr.getBytes()));

        HashMap<String, String> parseResult = new HashMap<String, String>();
        String lastKey = "";
        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            switch (event) {
                case KEY_NAME:
                    lastKey = parser.getString();
                    break;
                case VALUE_STRING:
                    parseResult.put(lastKey, parser.getString());
                    break;
                default:
                    break;
            }
        }

        return parseResult;
    }

    private ChatData buildJsonToChatData(String sessionId, Map<String, String> jsonMap) {

        String name = jsonMap.get("name");
        String textMsg = jsonMap.get("message");
        String usericon = jsonMap.get("usericon");
        Date date = new Date();

        return createChatData(String.valueOf(messageId), sessionId, name, usericon, textMsg, date);
    }

    private String buildJsonMessage(String status, ChatData data) {

        StringBuilder builder = new StringBuilder();
        builder.append("{ \"status\" : \"");
        builder.append(status);
        builder.append("\", \"messageid\" : \"");
        builder.append(data.getMessageid());
        builder.append("\", \"sessionid\" : \"");
        builder.append(data.getSessionid());
        builder.append("\", \"name\" : \"");
        builder.append(data.getName());
        builder.append("\", \"usericon\" : \"");
        builder.append(data.getUsericon());
        builder.append("\", \"message\" : \"");
        String message = data.getMessage();
        message = message.replace("\"", "&quot;");
        builder.append(message);
        builder.append("\", \"messagedate\" : \"");
        builder.append(format.format(data.getMessagedate()));
        builder.append("\" }");
        return builder.toString();
    }

}