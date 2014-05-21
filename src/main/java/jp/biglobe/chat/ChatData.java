package jp.biglobe.chat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sane on 2014/05/16.
 */
public class ChatData implements Serializable {

    private String messageid;
    private String sessionid;
    private String name;
    private String usericon;
    private String message;
    private Date messagedate;

    public ChatData(String messageid, String sessionid, String name, String usericon, String message, Date messagedate) {
        this.messageid = messageid;
        this.sessionid = sessionid;
        this.name = name;
        this.usericon = usericon;
        this.message = message;
        this.messagedate = messagedate;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsericon() {
        return usericon;
    }

    public void setUsericon(String usericon) {
        this.usericon = usericon;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getMessagedate() {
        return messagedate;
    }

    public void setMessagedate(Date messagedate) {
        this.messagedate = messagedate;
    }

    @Override
    public String toString() {
        return "ChatData { " +
                "messageid='" + messageid + '\'' +
                ", sessionid='" + sessionid + '\'' +
                ", name='" + name + '\'' +
                ", usericon='" + usericon + '\'' +
                ", message='" + message + '\'' +
                ", messagedate=" + messagedate +
                " }";
    }
}
