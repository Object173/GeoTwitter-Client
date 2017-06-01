package com.object173.geotwitter.server;

import com.object173.geotwitter.server.json.AuthData;
import com.object173.geotwitter.server.json.AuthProfile;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.server.json.AuthToken;
import com.object173.geotwitter.server.json.DialogJson;
import com.object173.geotwitter.server.json.Filter;
import com.object173.geotwitter.server.json.MessageJson;
import com.object173.geotwitter.server.json.NewPlaceJson;
import com.object173.geotwitter.server.json.PlaceFilter;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ServerApi {
    //авторизация пользователя
    @POST(ServerContract.REQUEST_SIGN_IN)
    Call<AuthResult> signIn(@Body AuthData authData);
    //регистрация пользователя
    @Multipart
    @POST(ServerContract.REQUEST_REGISTER)
    Call<AuthResult> register(@Part("authData") AuthData authData, @Part MultipartBody.Part imageFile);

    @Multipart
    @POST(ServerContract.REQUEST_SET_FCM_TOKEN)
    Call<Void> refreshFcmToken(@Part("authToken") AuthToken authToken, @Part("fcmToken") String fcmToken);

    @Multipart
    @POST(ServerContract.REQUEST_SET_USERNAME)
    Call<AuthResult.Result> setUsername(@Part("authToken") AuthToken authToken, @Part("username") String username);

    @Multipart
    @POST(ServerContract.REQUEST_SET_STATUS)
    Call<AuthResult.Result> setStatus(@Part("authToken") AuthToken authToken, @Part("status") String status);

    @Multipart
    @POST(ServerContract.REQUEST_SET_PASSWORD)
    Call<AuthResult.Result> setPassword(@Part("authToken") AuthToken authToken,
                                      @Part("oldPassword") String oldPassword, @Part("newPassword") String newPassword);

    @Multipart
    @POST(ServerContract.REQUEST_SET_AVATAR)
    Call<String> setAvatar(@Part("authToken") AuthToken authToken, @Part MultipartBody.Part avatar);

    @POST(ServerContract.REQUEST_REMOVE_AVATAR)
    Call<AuthResult.Result> removeAvatar(@Body AuthToken authToken);

    @Multipart
    @POST(ServerContract.REQUEST_GET_ALL_CONTACTS)
    Call<List<AuthProfile>> getAllContacts(@Part("authToken") AuthToken authToken, @Part("filter")Filter filter);

    @Multipart
    @POST(ServerContract.REQUEST_GET_ALL_CONTACTS)
    Call<List<AuthProfile>> getAllContacts(@Part("authToken") AuthToken authToken);

    @Multipart
    @POST(ServerContract.REQUEST_SEND_INVITE)
        Call<AuthProfile> sendInvite(@Part("authToken") AuthToken authToken, @Part("contactId") long contactId);

    @Multipart
    @POST(ServerContract.REQUEST_REMOVE_INVITE)
    Call<AuthProfile> removeInvite(@Part("authToken") AuthToken authToken, @Part("contactId") long contactId);

    @Multipart
    @POST(ServerContract.REQUEST_GET_DIALOG)
    Call<DialogJson> getDialog(@Part("authToken") AuthToken authToken, @Part("companionId") long companionId);

    @Multipart
    @POST(ServerContract.REQUEST_GET_ALL_DIALOGS)
    Call<List<DialogJson>> getAllDialogs(@Part("authToken") AuthToken authToken);

    @Multipart
    @POST(ServerContract.REQUEST_GET_MESSAGES)
    Call<List<MessageJson>> getMessageList(@Part("authToken") AuthToken authToken, @Part("dialogId") long dialogId,
                                        @Part("filter") Filter filter);

    @Multipart
    @POST(ServerContract.REQUEST_GET_LAST_MESSAGES)
    Call<List<MessageJson>> getLastMessages(@Part("authToken") AuthToken authToken, @Part("dialogId") long dialogId,
                                            @Part("lastId") long lastId);

    @Multipart
    @POST(ServerContract.REQUEST_SEND_MESSAGE)
    Call<MessageJson> sendMessage(@Part("authToken") AuthToken authToken, @Part("message") MessageJson message,
                                  @Part MultipartBody.Part imageFile);

    @Multipart
    @POST(ServerContract.REQUEST_ADD_PLACE)
    Call<NewPlaceJson> addPlace(@Part("authToken") AuthToken authToken, @Part("place") NewPlaceJson place,
                                @Part List<MultipartBody.Part> images);

    @Multipart
    @POST(ServerContract.REQUEST_GET_LAST_PLACE)
    Call<List<NewPlaceJson>> getTopPlaces(@Part("authToken") AuthToken authToken, @Part("authorId") long authorId,
                                           @Part("lastId") long lastId);

    @Multipart
    @POST(ServerContract.REQUEST_GET_LAST_ALL_PLACE)
    Call<List<NewPlaceJson>> getTopPlaces(@Part("authToken") AuthToken authToken, @Part("lastId") long lastId);

    @Multipart
    @POST(ServerContract.REQUEST_GET_LIST_PLACE)
    Call<List<NewPlaceJson>> getBottomPlaces(@Part("authToken") AuthToken authToken, @Part("authorId") long authorId,
                                           @Part("filter") Filter filter);

    @Multipart
    @POST(ServerContract.REQUEST_GET_LIST_ALL_PLACE)
    Call<List<NewPlaceJson>> getBottomPlaces(@Part("authToken") AuthToken authToken, @Part("filter") Filter filter);

    @Multipart
    @POST(ServerContract.REQUEST_GET_ALL_PLACE)
    Call<List<NewPlaceJson>> getAllPlaces(@Part("authToken") AuthToken authToken, @Part("filter") PlaceFilter filter);
}
