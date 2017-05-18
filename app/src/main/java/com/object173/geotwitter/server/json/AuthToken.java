package com.object173.geotwitter.server.json;

public class AuthToken {

    private long userId;
    private String hashKey;

    public AuthToken() {
    }

    public AuthToken(long userId, String hashKey) {
        this.userId = userId;
        this.hashKey = hashKey;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }
}
