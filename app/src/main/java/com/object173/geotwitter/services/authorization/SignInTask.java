package com.object173.geotwitter.services.authorization;

import android.content.Context;
import android.content.Intent;

import com.object173.geotwitter.GeoTwitterApp;
import com.object173.geotwitter.server.json.AuthData;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.services.BaseServiceTask;
import com.object173.geotwitter.util.NetworkConnectionManager;

/**
 * Created by Object173
 * on 27.04.2017.
 */

public final class SignInTask extends BaseServiceTask{
    private static final String TYPE = "sign_in";

    private static final String KEY_LOGIN = "login";
    private static final String KEY_PASSWORD = "password";

    private static final String KEY_RESULT = "result";

    SignInTask(final OnFinishTaskListener finishListener) {
        super(finishListener);
    }

    public static boolean isThisIntent(final Intent intent) {
        return BaseServiceTask.isThisIntent(intent, TYPE);
    }

    static Intent createInputIntent(final Context context, final String login,
                                    final String password, final long requestId) {
        if(context == null || login == null || password == null) {
            return null;
        }

        final Intent intent = BaseServiceTask.createBaseInputIntent(context, AuthService.class,
                AuthService.ACTION, TYPE, requestId);
        intent.putExtra(KEY_LOGIN, login);
        intent.putExtra(KEY_PASSWORD, password);
        return intent;
    }

    private Intent createResultIntent(final Intent intent, final AuthResult.Result result) {
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

        final String login = intent.getStringExtra(KEY_LOGIN);
        final String password = intent.getStringExtra(KEY_PASSWORD);

        final AuthData request = new AuthData(login, password);

        try {
            final AuthResult result = GeoTwitterApp.getApi().signIn(request).execute().body();
            finishTask(createResultIntent(intent, result.getResult()));
        } catch (Exception e) {
            e.printStackTrace();
            finishTask(createResultIntent(intent, AuthResult.Result.FAIL));
        }
    }

    public static AuthResult.Result getAuthResult(final Intent intent) {
        if(intent == null) {
            return AuthResult.Result.FAIL;
        }
        return (AuthResult.Result) intent.getSerializableExtra(KEY_RESULT);
    }
}
