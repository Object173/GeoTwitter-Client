package com.object173.geotwitter.server.json;

/**
 * Created by Object173
 * on 30.04.2017.
 */

public final class AuthData {

    private String username;
    private String login;
    private String password;
    private String fcmToken;

    public AuthData() {
    }

    public AuthData(String username, String login, String password, String fcmToken) {
        this.username = username;
        this.login = login;
        this.password = password;
        this.fcmToken = fcmToken;
    }

    public AuthData(String login, String password, String fcmToken) {
        this(null, login, password, fcmToken);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
