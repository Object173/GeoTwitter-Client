package com.object173.geotwitter.server;

import com.object173.geotwitter.server.json.AuthData;
import com.object173.geotwitter.server.json.AuthProfile;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.server.json.AuthToken;
import com.object173.geotwitter.server.json.Filter;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ServerApi {
    String HOST = "http://37.192.46.135:8443/";

    String AUTH_CONTROLLER = "/auth";
    String REQUEST_SIGN_IN = AUTH_CONTROLLER + "/sign_in/";
    String REQUEST_REGISTER = AUTH_CONTROLLER + "/register/";

    String RESOURCES_CONTROLLER = "/resources";

    String PROFILE_CONTROLLER = "/profile";
    String REQUEST_SET_FCM_TOKEN = PROFILE_CONTROLLER + "/set_fcm_token/";
    String REQUEST_SET_USERNAME = PROFILE_CONTROLLER + "/set_username/";
    String REQUEST_SET_STATUS = PROFILE_CONTROLLER + "/set_status/";
    String REQUEST_SET_PASSWORD = PROFILE_CONTROLLER + "/set_password/";
    String REQUEST_SET_AVATAR = PROFILE_CONTROLLER + "/set_avatar/";
    String REQUEST_REMOVE_AVATAR = PROFILE_CONTROLLER + "/remove_avatar/";

    String CONTACTS_CONTROLLER = "/contacts";
    String REQUEST_GET_ALL_CONTACTS = CONTACTS_CONTROLLER + "/get_all/";
    String REQUEST_SEND_INVITE = CONTACTS_CONTROLLER + "/send_invite/";
    String REQUEST_REMOVE_INVITE = CONTACTS_CONTROLLER + "/remove_invite/";

    String ATTR_AVATAR_NAME = "avatar";

    String MEDIA_TYPE_FILE = "multipart/form-data";

    @POST(REQUEST_SIGN_IN)
    Call<AuthResult> signIn(@Body AuthData authData);

    @Multipart
    @POST(REQUEST_REGISTER)
    Call<AuthResult> register(@Part("authData") AuthData authData, @Part MultipartBody.Part imageFile);

    @Multipart
    @POST(REQUEST_SET_FCM_TOKEN)
    Call<Void> refreshFcmToken(@Part("authToken") AuthToken authToken, @Part("fcmToken") String fcmToken);

    @Multipart
    @POST(REQUEST_SET_USERNAME)
    Call<AuthResult.Result> setUsername(@Part("authToken") AuthToken authToken, @Part("username") String username);

    @Multipart
    @POST(REQUEST_SET_STATUS)
    Call<AuthResult.Result> setStatus(@Part("authToken") AuthToken authToken, @Part("status") String status);

    @Multipart
    @POST(REQUEST_SET_PASSWORD)
    Call<AuthResult.Result> setPassword(@Part("authToken") AuthToken authToken,
                                      @Part("oldPassword") String oldPassword, @Part("newPassword") String newPassword);

    @Multipart
    @POST(REQUEST_SET_AVATAR)
    Call<String> setAvatar(@Part("authToken") AuthToken authToken, @Part MultipartBody.Part avatar);

    @POST(REQUEST_REMOVE_AVATAR)
    Call<AuthResult.Result> removeAvatar(@Body AuthToken authToken);

    @Multipart
    @POST(REQUEST_GET_ALL_CONTACTS)
    Call<List<AuthProfile>> getAllContacts(@Part("authToken") AuthToken authToken, @Part("filter")Filter filter);

    @Multipart
    @POST(REQUEST_GET_ALL_CONTACTS)
    Call<List<AuthProfile>> getAllContacts(@Part("authToken") AuthToken authToken);

    @Multipart
    @POST(REQUEST_SEND_INVITE)
        Call<AuthProfile> sendInvite(@Part("authToken") AuthToken authToken, @Part("contactId") long contactId);

@Multipart
@POST(REQUEST_REMOVE_INVITE)
    Call<AuthProfile> removeInvite(@Part("authToken") AuthToken authToken, @Part("contactId") long contactId);
        }
