package com.gojiyajayesh.chatvista.models;

public class UserAvailabilityModel {
    boolean isOnline;
    Long LastOfflineTime;

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean online) {
        isOnline = online;
    }

    public Long getLastOfflineTime() {
        return LastOfflineTime;
    }

    public void setLastOfflineTime(Long lastOfflineTime) {
        LastOfflineTime = lastOfflineTime;
    }

    public UserAvailabilityModel() {
    }
}
