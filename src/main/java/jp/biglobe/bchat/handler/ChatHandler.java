package jp.biglobe.bchat.handler;

import jp.biglobe.bchat.config.ApplicationConfig;
import jp.biglobe.bchat.model.ChatData;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Map.Entry;

public class ChatHandler extends TextWebSocketHandler {

    private static String STATUS_INPUTTING = "inputting";
    private static String STATUS_COMMIT = "commit";

    /** セッション一覧 */
    private Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap();

    /** 一意に振られるメッセージID */
    private static int messageId = 0;

    /** CHATデータ履歴。MESSAGE_MAXまで記録 */
    private List<ChatData> chatDataList = Collections.synchronizedList(new ArrayList<ChatData>());
    private static int MESSAGE_MAX = 100;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        this.sessionMap.put(session.getId(), session);

        // 接続と同時に今までのCHAT履歴を送る。
        sendAllMessages(session);
    }

    private void sendAllMessages(WebSocketSession session) throws Exception {

        for (ChatData chatData : chatDataList) {
            TextMessage sendMsg = new TextMessage(buildJsonMessage(STATUS_COMMIT, chatData));
            session.sendMessage(sendMsg);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        this.sessionMap.remove(session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        try {

            Map<String, String> jsonMap = buildMapFromJson(message.getPayload());
            ChatData chatData = buildChatDataFromJson(session.getId(), jsonMap);

            String status = jsonMap.get("status");

            if (STATUS_COMMIT.equals(status)) {
                messageId++;
                chatDataList.add(chatData);
                if (chatDataList.size() > MESSAGE_MAX) {
                    chatDataList.remove(0);
                }
                ApplicationConfig.getQueueHandler().sendObject(chatData);
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

    private Map<String, String> buildMapFromJson(String jsonStr) {
        return parseJson(jsonStr);
    }

    private ChatData buildChatDataFromJson(String sessionId, Map<String, String> jsonMap) {

        String name = jsonMap.get("name");
        String textMsg = jsonMap.get("message");
        String usericon = jsonMap.get("usericon");
        Date date = new Date();

        return createChatData(String.valueOf(messageId), sessionId, name, usericon, textMsg, date);
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

    private String buildJsonMessage(String status, ChatData data) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

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