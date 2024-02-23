package com.gojiyajayesh.chatvista.models;

public class Users {
    String profilePicture, userName, password, mail, userId, lastMessage;

    public Users(String profilePicture, String userName, String password, String mail, String userId, String lastMessage) {
        this.profilePicture = profilePicture;
        this.userName = userName;
        this.password = password;
        this.mail = mail;
        this.userId = userId;
        this.lastMessage = lastMessage;
    }

    // SignUp Constructor
    public Users(String userName, String mail, String password) {
        this.userName = userName;
        this.password = password;
        this.mail = mail;
    }

    public Users() {
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
