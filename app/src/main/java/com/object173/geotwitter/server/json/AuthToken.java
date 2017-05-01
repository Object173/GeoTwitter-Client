package com.object173.geotwitter.server.json;

import java.io.Serializable;

public class AuthToken implements Serializable{

    private long user_id;
    private String hash_key;

    public AuthToken() {
    }

    public AuthToken(long user_id, String hash_key) {
        this.user_id = user_id;
        this.hash_key = hash_key;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getHash_key() {
        return hash_key;
    }

    public void setHash_key(String hash_key) {
        this.hash_key = hash_key;
    }
}
