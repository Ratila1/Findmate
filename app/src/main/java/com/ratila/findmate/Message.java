package com.ratila.findmate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Message {
    private String sender; // Должно называться sender, как в правилах
    private String userName;
    private String textMessage;
    private long messageTime;

    public Message() {
        // Пустой конструктор для Firebase
    }

    public Message(String sender, String userName, String textMessage, long messageTime) {
        this.sender = sender;
        this.userName = userName;
        this.textMessage = textMessage;
        this.messageTime = messageTime;
    }

    public String getSender() {
        return sender;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public long getMessageTime() {
        return messageTime;
    }
}