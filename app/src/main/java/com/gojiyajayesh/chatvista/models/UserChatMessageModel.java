package com.gojiyajayesh.chatvista.models;

public class UserChatMessageModel {
    private String message;
    private String userId;
    private Long messageTime;
    private String receiverId;

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public UserChatMessageModel() {
    }

    public UserChatMessageModel(String message, String userId) {
        this.message = message;
        this.userId = userId;
    }

    public UserChatMessageModel(String message, String userId,String receiverId, Long messageTime) {
        this.message = message;
        this.userId = userId;
        this.messageTime = messageTime;
        this.receiverId=receiverId;
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
