package com.gojiyajayesh.chatvista.models;

public class Users {
    String UserId, Username,LastMessage,ProfileId,Password,Email;
    Long LastMessageTime;
    public Long getLastMessageTime() {
        return LastMessageTime;
    }

    public Users(String lastMessage, Long lastMessageTime) {
        LastMessage = lastMessage;
        LastMessageTime = lastMessageTime;
    }

    public void setLastMessageTime(Long lastMessageTime) {
        LastMessageTime = lastMessageTime;
    }

    public Users(String UserId, String username, String profileId, String email, String password) {
        ProfileId = profileId;
        Password = password;
        Email = email;
        Username =username;
       this.UserId=UserId;
    }
    public Users(){}
    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getLastMessage() {
        return LastMessage;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }

    public String getProfileId() {
        return ProfileId;
    }

    public void setProfileId(String profileId) {
        ProfileId = profileId;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
