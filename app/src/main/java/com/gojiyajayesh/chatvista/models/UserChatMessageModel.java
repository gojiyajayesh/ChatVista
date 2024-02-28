package com.gojiyajayesh.chatvista.models;

public class UserChatMessageModel {
    String Message,UserId;
    Long MessageTime;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public Long getMessageTime() {
        return MessageTime;
    }

    public void setMessageTime(Long messageTime) {
        MessageTime = messageTime;
    }

    public UserChatMessageModel(String message, String userId) {
        Message = message;
        UserId = userId;
    }

    public UserChatMessageModel(String message, String userId, Long messageTime) {
        Message = message;
        UserId = userId;
        MessageTime = messageTime;
    }
    public UserChatMessageModel(){}
}
