package com.gojiyajayesh.chatvista.models;

public class UserStatusModel {
    String ProfileId,Username,StatusUrl;
    String StatusUpdateTime;

    public UserStatusModel(String profileId, String username, String statusUrl, String statusUpdateTime) {
        ProfileId = profileId;
        Username = username;
        StatusUrl = statusUrl;
        StatusUpdateTime = statusUpdateTime;
    }

    public String getProfileId() {
        return ProfileId;
    }

    public void setProfileId(String profileId) {
        ProfileId = profileId;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getStatusUrl() {
        return StatusUrl;
    }

    public void setStatusUrl(String statusUrl) {
        StatusUrl = statusUrl;
    }

    public String getStatusUpdateTime() {
        return StatusUpdateTime;
    }

    public void setStatusUpdateTime(String statusUpdateTime) {
        StatusUpdateTime = statusUpdateTime;
    }

    public UserStatusModel() {
    }
}
