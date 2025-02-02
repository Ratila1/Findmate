package com.ratila.findmate;

public class ChatItem {
    private String chatId;
    private String chatName;
    private String lastMessage;
    private int avatarResource;

    public ChatItem(String chatId, String chatName, String lastMessage, int avatarResource) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.lastMessage = lastMessage;
        this.avatarResource = avatarResource;
    }

    public String getChatId() {
        return chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getAvatarResource() {
        return avatarResource;
    }
}
