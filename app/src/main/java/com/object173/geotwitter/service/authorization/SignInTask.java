package com.object173.geotwitter.service.authorization;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.object173.geotwitter.GeoTwitterApp;
import com.object173.geotwitter.server.json.AuthData;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.service.BaseServiceTask;
import com.object173.geotwitter.service.notification.InstanceIDService;
import com.object173.geotwitter.util.NetworkConnectionManager;
import com.object173.geotwitter.util.user.AuthManager;

/**
 * Created by Object173
 * on 27.04.2017.
 */

public final class SignInTask extends BaseServiceTask {
    private static final String TYPE = "sign_in";

    private static final String KEY_LOGIN = "login";
    private static final String KEY_PASSWORD = "password";

    public static boolean isThisIntent(final Intent intent) {
        return BaseServiceTask.isThisIntent(intent, TYPE);
    }

    public static Intent createInputIntent(final Context context, final String login,
                                    final String password, final long requestId) {
        if(context == null || login == null || password == null) {
            return null;
        }

        final Intent intent = createBaseInputIntent(context, TYPE, requestId);
        intent.putExtra(KEY_LOGIN, login);
        intent.putExtra(KEY_PASSWORD, password);
        return intent;
    }

    public static Intent startTask(final Context context, final Intent intent) {
        if (intent == null || context == null) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        if(!NetworkConnectionManager.isConnection(context)) {
            return createResultIntent(intent, AuthResult.Result.NO_INTERNET);
        }

        final String login = intent.getStringExtra(KEY_LOGIN);
        final String password = intent.getStringExtra(KEY_PASSWORD);

        final AuthData request = new AuthData(login, password, InstanceIDService.getToken(context));

        try {
            final AuthResult result = GeoTwitterApp.getApi().signIn(request).execute().body();
            if(result.getResult() == AuthResult.Result.SUCCESS) {
                if(AuthManager.setCurrentUser(
                        context, result.getProfile(), result.getToken(), login, password)) {
                    return createResultIntent(intent, AuthResult.Result.SUCCESS);
                }
                else {
                    return createResultIntent(intent, AuthResult.Result.FAIL);
                }
            }
            Log.d("register", result.getResult().toString());
            return createResultIntent(intent, result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
            return createResultIntent(intent, AuthResult.Result.FAIL);
        }
    }
}
