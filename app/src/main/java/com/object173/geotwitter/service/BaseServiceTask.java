package com.object173.geotwitter.service;

import android.content.Context;
import android.content.Intent;

import com.object173.geotwitter.server.json.AuthResult;

public class BaseServiceTask {
    private static final String KEY_REQUEST_ID = "request_id";
    private static final String KEY_RESULT = "result";
    private static final String KEY_TYPE = "type";

    protected static final int NULL_ID = BaseService.NULL_ID;

    protected static Intent createBaseInputIntent(final Context context, final String type, final long request_id) {
        final Intent intent = new Intent(context, BaseService.class);
        intent.setAction(BaseService.ACTION);
        intent.putExtra(KEY_TYPE, type);
        intent.putExtra(KEY_REQUEST_ID, request_id);
        return intent;
    }

    protected static Intent createOutputIntent(final Intent inIntent) {
        final Intent intent = new Intent();
        intent.setAction(inIntent.getAction());
        intent.putExtra(KEY_TYPE, inIntent.getStringExtra(KEY_TYPE));
        intent.putExtra(KEY_REQUEST_ID, getRequestId(inIntent));

        return intent;
    }

    protected static boolean isThisIntent(final Intent intent, final String type) {
        return intent != null && intent.getStringExtra(KEY_TYPE).equals(type);
    }

    public static long getRequestId(final Intent intent) {
        if(intent != null) {
            return intent.getLongExtra(KEY_REQUEST_ID,(long)NULL_ID);
        }
        return (long)NULL_ID;
    }

    protected static Intent createResultIntent(final Intent intent, final AuthResult.Result result) {
        if(intent == null) {
            return null;
        }
        final Intent outIntent = BaseServiceTask.createOutputIntent(intent);
        outIntent.putExtra(KEY_RESULT, result);
        return outIntent;
    }

    public static AuthResult.Result getAuthResult(final Intent intent) {
        if(intent == null) {
            return AuthResult.Result.FAIL;
        }
        final AuthResult.Result result = (AuthResult.Result)intent.getSerializableExtra(KEY_RESULT);
        if(result == null) {
            return AuthResult.Result.FAIL;
        }
        return result;
    }
}
