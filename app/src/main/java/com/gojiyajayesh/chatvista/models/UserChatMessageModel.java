package com.gojiyajayesh.chatvista.models;

public class UserChatMessageModel {
    private String message;
    private String userId;
    private Long messageTime;

    public UserChatMessageModel() {
    }

    public UserChatMessageModel(String message, String userId) {
        this.message = message;
        this.userId = userId;
    }

    public UserChatMessageModel(String message, String userId, Long messageTime) {
        this.message = message;
        this.userId = userId;
        this.messageTime = messageTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Long messageTime) {
        this.messageTime = messageTime;
    }
}
