package com.gojiyajayesh.chatvista.models;

import com.google.firebase.Timestamp;

public class MessageModel {
    String message;
    Timestamp lastMessageTime;
    String SenderId;
    public MessageModel(String message, Timestamp lastMessageTime, String senderId) {
        this.message = message;
        this.lastMessageTime = lastMessageTime;
        SenderId = senderId;
    }

    public MessageModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Timestamp lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }
}
