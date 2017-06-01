package com.object173.geotwitter.service.authorization;

import android.content.Context;
import android.content.Intent;

import com.object173.geotwitter.GeoTwitterApp;
import com.object173.geotwitter.server.ServerContract;
import com.object173.geotwitter.server.json.AuthData;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.service.BaseServiceTask;
import com.object173.geotwitter.service.notification.InstanceIDService;
import com.object173.geotwitter.util.NetworkConnectionManager;
import com.object173.geotwitter.util.user.AuthManager;
import com.object173.geotwitter.util.user.AvatarManager;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Object173
 * on 28.04.2017.
 */

public final class RegisterTask extends BaseServiceTask {
    private static final String TYPE = "register";

    private static final String KEY_USERNAME = "username";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_AVATAR = "avatar";

    public static boolean isThisIntent(final Intent intent) {
        return BaseServiceTask.isThisIntent(intent, TYPE);
    }

    public static Intent createInputIntent(final Context context, final String username, final String login,
                                    final String password, final String avatar, final long requestId) {
        if(context == null || username == null || password == null || login == null) {
            return null;
        }

        final Intent intent = BaseServiceTask.createBaseInputIntent(context, TYPE, requestId);
        intent.putExtra(KEY_USERNAME, username);
        intent.putExtra(KEY_LOGIN,login);
        intent.putExtra(KEY_PASSWORD,password);
        intent.putExtra(KEY_AVATAR, avatar);
        return intent;
    }

    public static Intent startTask(final Context context, final Intent intent) {
        if (intent == null || context == null) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        if(!NetworkConnectionManager.isConnection(context)) {
            return createResultIntent(intent, AuthResult.Result.NO_INTERNET);
        }

        final String username = intent.getStringExtra(KEY_USERNAME);
        final String login = intent.getStringExtra(KEY_LOGIN);
        final String password = intent.getStringExtra(KEY_PASSWORD);
        final String avatarPath = intent.getStringExtra(KEY_AVATAR);

        AvatarManager.writeAvatar(context, avatarPath);

        final MultipartBody.Part avatarPart;

        final File file = new File(AvatarManager.getAvatarPath());
        if(avatarPath != null && file.exists()) {
            RequestBody avatarFile = RequestBody.create(MediaType.parse(ServerContract.MEDIA_TYPE_FILE), file);
            avatarPart = MultipartBody.Part.createFormData(
                    ServerContract.ATTR_AVATAR_NAME, file.getName(), avatarFile);
        }
        else {
            avatarPart = null;
        }

        if(username == null || login == null || password == null) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        final AuthData authData = new AuthData(username, login, password, InstanceIDService.getToken(context));

        try {
            final AuthResult result = GeoTwitterApp.getApi().register(authData, avatarPart).execute().body();
            if(result.getResult() == AuthResult.Result.SUCCESS) {
                if(AuthManager.setCurrentUser(context, result.getProfile(), result.getToken(), login, password)) {
                    return createResultIntent(intent, AuthResult.Result.SUCCESS);
                }
                else {
                    return createResultIntent(intent, AuthResult.Result.FAIL);
                }
            }
            return createResultIntent(intent, result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
            return createResultIntent(intent, AuthResult.Result.FAIL);
        }
    }
}
