package com.ratila.findmate;

public class ChatItem {
    private String chatId;
    private String name;
    private String lastMessage;
    private int avatar;
    private long messageTime; // Время сообщения
    private boolean isUserMessage; // Поле для определения, чьё сообщение

    // Конструктор для создания объекта без времени сообщения
    public ChatItem(String chatId, String name, String lastMessage, int avatar, boolean isUserMessage) {
        this.chatId = chatId;
        this.name = name;
        this.lastMessage = lastMessage;
        this.avatar = avatar;
        this.messageTime = 0; // Устанавливаем значение по умолчанию для времени сообщения
        this.isUserMessage = isUserMessage; // Определяем, чьё сообщение
    }

    // Конструктор с временем сообщения
    public ChatItem(String chatId, String name, String lastMessage, int avatar, long messageTime, boolean isUserMessage) {
        this.chatId = chatId;
        this.name = name;
        this.lastMessage = lastMessage;
        this.avatar = avatar;
        this.messageTime = messageTime;
        this.isUserMessage = isUserMessage; // Определяем, чьё сообщение
    }

    // Геттеры и сеттеры
    public String getChatId() {
        return chatId;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getAvatar() {
        return avatar;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public boolean isUserMessage() {
        return isUserMessage;
    }

    public void setUserMessage(boolean isUserMessage) {
        this.isUserMessage = isUserMessage;
    }
}
