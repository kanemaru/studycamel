package jp.biglobe.bchat.model;

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

    public String getSessionid() {
        return sessionid;
    }

    public String getName() {
        return name;
    }

    public String getUsericon() {
        return usericon;
    }

    public String getMessage() {
        return message;
    }

    public Date getMessagedate() {
        return messagedate;
    }

    @Override
    public String toString() {
        return "ChatData { " +
                "messageid='" + getMessageid() + '\'' +
                ", sessionid='" + getSessionid() + '\'' +
                ", name='" + getName() + '\'' +
                ", usericon='" + getUsericon() + '\'' +
                ", message='" + getMessage() + '\'' +
                ", messagedate=" + getMessagedate() +
                " }";
    }

}
