package com.object173.geotwitter.services.authorization;

import android.content.Context;
import android.content.Intent;

import com.object173.geotwitter.services.BaseService;

/**
 * Created by Object173
 * on 27.04.2017.
 */

public final class AuthService extends BaseService {

    private static final int POOL_SIZE = 1;

    public AuthService() {
        super(POOL_SIZE);
    }

    public static long startToSignIn(final Context context, final String login, final String password) {
        if(context == null || login == null || password == null) {
            return NULL_ID;
        }
        final long requestId = createRequestId();
        final Intent intent = SignInTask.createInputIntent(context, login, password, requestId);
        return startToIntent(context, intent, requestId);
    }

    public static long startToRegister(final Context context, final String username,
                                       final String login, final String password, final String avatar) {
        if(context == null || username == null || login == null || password == null) {
            return NULL_ID;
        }
        final long requestId = createRequestId();
        final Intent intent = RegisterTask.createInputIntent(
                context, username, login, password, avatar, requestId);
        return startToIntent(context, intent, requestId);
    }

    @Override
    protected boolean onHandleIntent(final Intent intent, final ServiceTask task) {
        if(intent == null || task == null) {
            return false;
        }
        if(SignInTask.isThisIntent(intent)) {
            final SignInTask signInTask = new SignInTask(task);
            signInTask.startTask(getBaseContext(), intent);
        } else
        if(RegisterTask.isThisIntent(intent)) {
            final RegisterTask registerTask = new RegisterTask(task);
            registerTask.startTask(getBaseContext(), intent);
        }
        return true;
    }
}
