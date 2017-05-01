package com.object173.geotwitter.services.authorization;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.object173.geotwitter.GeoTwitterApp;
import com.object173.geotwitter.server.ServerApi;
import com.object173.geotwitter.server.json.AuthData;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.services.BaseServiceTask;
import com.object173.geotwitter.util.ImageUtils;
import com.object173.geotwitter.util.ImagesManager;
import com.object173.geotwitter.util.NetworkConnectionManager;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Object173
 * on 28.04.2017.
 */

public final class RegisterTask extends BaseServiceTask{
    private static final String TYPE = "register";

    private static final String KEY_USERNAME = "username";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_AVATAR = "avatar";

    private static final String KEY_RESULT = "result";

    RegisterTask(final BaseServiceTask.OnFinishTaskListener finishListener) {
        super(finishListener);
    }

    public static boolean isThisIntent(final Intent intent) {
        return BaseServiceTask.isThisIntent(intent, TYPE);
    }

    static Intent createInputIntent(final Context context, final String username, final String login,
                                    final String password, final String avatar, final long requestId) {
        if(context == null || username == null || password == null || login == null) {
            return null;
        }

        final Intent intent = BaseServiceTask.createBaseInputIntent(context, AuthService.class,
                AuthService.ACTION, TYPE, requestId);
        intent.putExtra(KEY_USERNAME, username);
        intent.putExtra(KEY_LOGIN,login);
        intent.putExtra(KEY_PASSWORD,password);
        intent.putExtra(KEY_AVATAR, avatar);
        return intent;
    }

    private Intent createResultIntent(final Intent intent, final com.object173.geotwitter.server.json.AuthResult.Result result) {
        if(intent == null || result == null) {
            return null;
        }
        final Intent outIntent = BaseServiceTask.createOutputIntent(intent);
        outIntent.putExtra(KEY_RESULT, result);
        return outIntent;
    }

    final void startTask(final Context context, final Intent intent) {
        if (intent == null || context == null) {
            finishTask(createResultIntent(intent, AuthResult.Result.NULL_POINTER));
            return;
        }

        if(!NetworkConnectionManager.isConnection(context)) {
            finishTask(createResultIntent(intent, AuthResult.Result.NO_INTERNET));
            return;
        }

        final String username = intent.getStringExtra(KEY_USERNAME);
        final String login = intent.getStringExtra(KEY_LOGIN);
        final String password = intent.getStringExtra(KEY_PASSWORD);
        final String avatarPath = intent.getStringExtra(KEY_AVATAR);

        final Bitmap avatar = ImageUtils.resizeBitmap(
                ImagesManager.readImage(avatarPath), ImageUtils.LARGE_WIDTH);
        ImagesManager.writeImage(context, avatar, avatarPath);

        final MultipartBody.Part avatarPart;

        if(avatarPath != null) {
            final File file = new File(Uri.parse(avatarPath).getPath());
            RequestBody avatarFile = RequestBody.create(MediaType.parse(ServerApi.MEDIA_TYPE_FILE), file);
            avatarPart = MultipartBody.Part.createFormData(
                    ServerApi.ATTR_AVATAR_NAME, file.getName(), avatarFile);
        }
        else {
            avatarPart = null;
        }

        if(username == null || login == null || password == null) {
            finishTask(createResultIntent(intent, AuthResult.Result.NULL_POINTER));
            return;
        }

        final AuthData authData = new AuthData(username, login, password);

        try {
            final AuthResult result = GeoTwitterApp.getApi().register(authData, avatarPart).execute().body();
            finishTask(createResultIntent(intent, result.getResult()));
        } catch (Exception e) {
            e.printStackTrace();
            finishTask(createResultIntent(intent, AuthResult.Result.FAIL));
        }
    }

    public static com.object173.geotwitter.server.json.AuthResult.Result getAuthResult(final Intent intent) {
        if(intent == null) {
            return com.object173.geotwitter.server.json.AuthResult.Result.FAIL;
        }
        return (com.object173.geotwitter.server.json.AuthResult.Result) intent.getSerializableExtra(KEY_RESULT);
    }
}
