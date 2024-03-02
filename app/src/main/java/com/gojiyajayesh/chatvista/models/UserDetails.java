package com.gojiyajayesh.chatvista.models;

public class UserDetails {
    private String userId;
    private String username;

    public UserDetails() {
        // Default constructor required for Firestore
    }

    public UserDetails(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
