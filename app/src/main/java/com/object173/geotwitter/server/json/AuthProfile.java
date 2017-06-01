package com.object173.geotwitter.server.json;

import java.io.Serializable;

public class AuthProfile implements Serializable{

    private long userId;
    private String username;
    private String status;
    private String avatarUrl;
    private String avatarUrlMini;
    private RelationStatus relation;

    public enum RelationStatus{
        NONE {
            @Override
            public int toInt() {
                return 0;
            }
        },
        CONTACT {
            @Override
            public int toInt() {
                return 1;
            }},
        SUBSCRIBER {
            @Override
            public int toInt() {
                return 2;
            }
        },
        INVITE {
            @Override
            public int toInt() {
                return 3;
            }
        };

        public int toInt() {
            return -1;
        }
    }

    public AuthProfile() {
    }

    public AuthProfile(String username, String status, String avatarUrl) {
        this.username = username;
        this.status = status;
        this.avatarUrl = avatarUrl;
        this.avatarUrlMini = avatarUrl;
    }

    public AuthProfile(String username, String status, String avatarUrl, String avatarUrlMini) {
        this.username = username;
        this.status = status;
        this.avatarUrl = avatarUrl;
        this.avatarUrlMini = avatarUrlMini;
    }

    public AuthProfile(String username, String status) {
        this.username = username;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrlMini() {
        return avatarUrlMini;
    }

    public void setAvatarUrlMini(String avatarUrlMini) {
        this.avatarUrlMini = avatarUrlMini;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public RelationStatus getRelation() {
        return relation;
    }

    public void setRelation(RelationStatus relation) {
        this.relation = relation;
    }
}
