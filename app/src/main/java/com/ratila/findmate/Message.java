package com.ratila.findmate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Message {
    private String userName;
    private String textMessage;
    private long messageTime;

    public Message() {
        // Пустой конструктор необходим для Firebase
    }

    public Message(String userName, String textMessage) {
        this.userName = userName;
        this.textMessage = textMessage;
        this.messageTime = System.currentTimeMillis();
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
