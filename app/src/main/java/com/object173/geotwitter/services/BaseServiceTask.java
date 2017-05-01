package com.object173.geotwitter.services;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Object173
 * on 28.03.2017.
 */

public class BaseServiceTask {
    private static final String KEY_REQUEST_ID = "request_id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_RESULT = "result";

    protected static final int NULL_ID = BaseService.NULL_ID;

    private OnFinishTaskListener finishListener;

    public interface OnFinishTaskListener {
        void finishTask(Intent intent);
    }

    public BaseServiceTask(final OnFinishTaskListener finishListener) {
        this.finishListener = finishListener;
    }

    protected void finishTask(final Intent intent) {
        if(finishListener != null) {
            finishListener.finishTask(intent);
            finishListener = null;
        }
    }

    protected static Intent createBaseInputIntent(final Context context, final Class cls,  final String serviceAction,
                                                  final String type, final long request_id) {
        final Intent intent = new Intent(context, cls);
        intent.setAction(serviceAction);
        intent.putExtra(KEY_TYPE, type);
        intent.putExtra(KEY_REQUEST_ID, request_id);
        return intent;
    }

    protected static Intent createOutputIntent(final Intent inIntent, final int result) {
        final Intent intent = new Intent();
        intent.setAction(inIntent.getAction());
        intent.putExtra(KEY_TYPE, inIntent.getStringExtra(KEY_TYPE));
        intent.putExtra(KEY_RESULT, result);
        intent.putExtra(KEY_REQUEST_ID, getRequestId(inIntent));

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
}
