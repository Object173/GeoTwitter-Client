package com.object173.geotwitter.server.json;

public class AuthResult {

    public enum Result {
        SUCCESS,
        FAIL,
        USER_NOT_FOUND,
        WRONG_PASSWORD,
        INCORRECT_USERNAME,
        INCORRECT_LOGIN,
        INCORRECT_PASSWORD,
        LOGIN_EXISTS,
        NULL_POINTER,
        NO_INTERNET,
        ACCESS_DENIED
    }

    private Result result;

    private AuthProfile profile;
    private AuthToken token;

    public AuthResult() {
    }

    public AuthResult(final Result result, final AuthProfile profile, final AuthToken token) {
        this.result = result;
        this.profile = profile;
        this.token = token;
    }

    public AuthResult(final Result result) {
        this(result, null, null);
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public AuthToken getToken() {
        return token;
    }

    public void setToken(AuthToken token) {
        this.token = token;
    }

    public AuthProfile getProfile() {
        return profile;
    }

    public void setProfile(AuthProfile profile) {
        this.profile = profile;
    }
}

