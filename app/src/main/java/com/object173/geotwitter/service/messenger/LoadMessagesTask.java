package com.object173.geotwitter.service.messenger;

import android.content.Context;
import android.content.Intent;

import com.object173.geotwitter.GeoTwitterApp;
import com.object173.geotwitter.database.entities.Message;
import com.object173.geotwitter.database.service.DialogService;
import com.object173.geotwitter.database.service.MessageService;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.server.json.Filter;
import com.object173.geotwitter.server.json.MessageJson;
import com.object173.geotwitter.service.BaseServiceTask;
import com.object173.geotwitter.util.NetworkConnectionManager;
import com.object173.geotwitter.util.user.AuthManager;

import java.util.List;

public final class LoadMessagesTask extends BaseServiceTask {

    private static final String TYPE_GET_LAST = "get_last_messages";
    private static final String TYPE_LOAD_LIST = "load_message_list";

    private static final String KEY_DIALOG_ID = "dialog_id";
    private static final String KEY_FILTER= "filter";

    public static boolean isThisIntent(final Intent intent) {
        return isGetLastIntent(intent) || isLoadListIntent(intent);
    }

    public static boolean isGetLastIntent(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_GET_LAST);
    }

    public static boolean isLoadListIntent(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_LOAD_LIST);
    }

    public static Intent createGetLastIntent(final Context context, final long dialogId, final long requestId) {
        if(context == null) {
            return null;
        }
        final Intent intent = BaseServiceTask.createBaseInputIntent(context, TYPE_GET_LAST, requestId);
        intent.putExtra(KEY_DIALOG_ID, dialogId);
        return intent;
    }

    public static Intent createLoadListIntent(final Context context, final long dialogId,
                                              final Filter filter, final long requestId) {
        if(context == null) {
            return null;
        }
        final Intent intent = BaseServiceTask.createBaseInputIntent(context, TYPE_LOAD_LIST, requestId);
        intent.putExtra(KEY_DIALOG_ID, dialogId);
        intent.putExtra(KEY_FILTER, filter);
        return intent;
    }

    public static Intent startTask(final Context context, final Intent intent) {
        if (intent == null || context == null || !AuthManager.isAuth()) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        if(!NetworkConnectionManager.isConnection(context)) {
            return createResultIntent(intent, AuthResult.Result.NO_INTERNET);
        }

        if(isGetLastIntent(intent)) {
            return startGetLastTask(context, intent);
        } else
        if(isLoadListIntent(intent)) {
            return startLoadListTask(context, intent);
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }

    public static Intent startGetLastTask(final Context context, final Intent intent) {
        if (intent == null || context == null || !AuthManager.isAuth()) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        final long dialogId = intent.getLongExtra(KEY_DIALOG_ID, NULL_ID);
        final Message lastMessage = MessageService.getLastMessage(context, dialogId);
        final long lastMessageId;
        if(lastMessage != null) {
            lastMessageId = lastMessage.getGlobalId();
        }
        else {
            lastMessageId = NULL_ID;
        }

        try {
            final List<MessageJson> result = GeoTwitterApp.getApi()
                            .getLastMessages(AuthManager.getAuthToken(), dialogId, lastMessageId)
                            .execute().body();
            if(result != null) {
                MessageService.addMessages(context, result, true);
                DialogService.invalidateDialog(context, dialogId);
                return createResultIntent(intent, AuthResult.Result.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }

    public static Intent startLoadListTask(final Context context, final Intent intent) {
        if (intent == null || context == null || !AuthManager.isAuth()) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }
        final long dialogId = intent.getLongExtra(KEY_DIALOG_ID, NULL_ID);
        final Filter filter = (Filter) intent.getSerializableExtra(KEY_FILTER);

        try {
            final List<MessageJson> result = GeoTwitterApp.getApi()
                    .getMessageList(AuthManager.getAuthToken(), dialogId, filter).execute().body();
            if(result != null) {
                MessageService.addMessages(context, result, false);
                DialogService.invalidateDialog(context, dialogId);
                return createResultIntent(intent, AuthResult.Result.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }
}
