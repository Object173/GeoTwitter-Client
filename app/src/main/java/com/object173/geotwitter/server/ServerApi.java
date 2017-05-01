package com.object173.geotwitter.server;

import com.object173.geotwitter.server.json.AuthData;
import com.object173.geotwitter.server.json.AuthResult;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Object173
 * on 30.04.2017.
 */

public interface ServerApi {
    String HOST = "http://37.192.46.135:8443/";

    String AUTH_CONTROLLER = "/auth";

    String REQUEST_SIGN_IN = AUTH_CONTROLLER + "/sign_in/";
    String REQUEST_REGISTER = AUTH_CONTROLLER + "/register/";
    String ATTR_AVATAR_NAME = "avatar";

    String MEDIA_TYPE_FILE = "multipart/form-data";

    @POST(REQUEST_SIGN_IN)
    Call<AuthResult> signIn(@Body AuthData authData);

    @Multipart
    @POST(REQUEST_REGISTER)
    Call<AuthResult> register(@Part("authData") AuthData authData, @Part MultipartBody.Part imageFile);
}
