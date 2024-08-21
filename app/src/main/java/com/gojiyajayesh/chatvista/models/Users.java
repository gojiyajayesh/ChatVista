package com.gojiyajayesh.chatvista.models;

public class Users implements Comparable<Users> {
    String StatusUrl;
    private String userId;
    private String username;
    private String lastMessage;
    private String profileId;
    private String password;
    private String phoneOrEmail;
    private String fullName;
    private Long lastMessageTime;
    private Long lastStatusUpdateTime;

    public Users(String userId, String username, String fullName, String profileId) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.profileId = profileId;
    }

    public Users(String phoneOrEmail, String password, String profileId, String username, String fullName, String userId) {
        this.phoneOrEmail = phoneOrEmail;
        this.password = password;
        this.profileId = profileId;
        this.username = username;
        this.fullName = fullName;
        this.userId = userId;
    }

    public Users(String lastMessage, Long lastMessageTime) {
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public Users(String userId, String username, String profileId, String email, String password) {
        this.profileId = profileId;
        this.password = password;
        this.phoneOrEmail = email;
        this.username = username;
        this.userId = userId;
    }

    public Users() {
    }

    @Override
    public int compareTo(Users other) {
        // Handle null lastMessageTime
        Long thisMessageTime = this.lastMessageTime != null ? this.lastMessageTime : Long.MIN_VALUE;
        Long otherMessageTime = other.getLastMessageTime() != null ? other.getLastMessageTime() : Long.MIN_VALUE;

        return Long.compare(otherMessageTime, thisMessageTime);
    }

    public Long getLastStatusUpdateTime() {
        return lastStatusUpdateTime;
    }

    public void setLastStatusUpdateTime(Long lastStatusUpdateTime) {
        this.lastStatusUpdateTime = lastStatusUpdateTime;
    }

    public String getStatusUrl() {
        return StatusUrl;
    }

    public void setStatusUrl(String statusUrl) {
        StatusUrl = statusUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneOrEmail() {
        return phoneOrEmail;
    }

    public void setPhoneOrEmail(String phoneOrEmail) {
        this.phoneOrEmail = phoneOrEmail;
    }
}
