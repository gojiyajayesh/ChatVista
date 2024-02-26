package com.gojiyajayesh.chatvista.models;

public class Users {
    String profileId;

    public Users(String username, String lastMessage) {
        this.username = username;
        this.lastMessage = lastMessage;
    }

    String username;
    String lastMessage;

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(String messageCount) {
        this.messageCount = messageCount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUserId(String key) {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    String messageCount;
    String time;
    String password;
    String mail;
    String userId;

    public Users(String profilePicture, String userName, String password, String mail, String userId, String lastMessage) {
        this.profileId = profilePicture;
        this.username = userName;
        this.password = password;
        this.mail = mail;
        this.userId = userId;
        this.lastMessage = lastMessage;
    }

    // SignUp Constructor
    public Users(String userName, String mail, String password) {
        this.username = userName;
        this.password = password;
        this.mail = mail;
    }

    public Users() {
    }
}
