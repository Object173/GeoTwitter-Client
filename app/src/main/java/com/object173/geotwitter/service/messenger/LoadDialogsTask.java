package com.object173.geotwitter.service.messenger;


import android.content.Context;
import android.content.Intent;

import com.object173.geotwitter.GeoTwitterApp;
import com.object173.geotwitter.database.entities.Dialog;
import com.object173.geotwitter.database.service.DialogService;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.server.json.DialogJson;
import com.object173.geotwitter.service.BaseServiceTask;
import com.object173.geotwitter.util.NetworkConnectionManager;
import com.object173.geotwitter.util.user.AuthManager;

import java.util.List;

public final class LoadDialogsTask extends BaseServiceTask{

    private static final String TYPE_GET = "get_dialog";
    private static final String TYPE_UPDATE = "update_dialog";

    private static final String KEY_COMPANION = "companion_id";
    private static final String KEY_DIALOG_ID = "dialog_id";

    public static boolean isThisIntent(final Intent intent) {
        return isGetIntent(intent) || isUpdateIntent(intent);
    }

    public static boolean isGetIntent(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_GET);
    }

    public static boolean isUpdateIntent(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_UPDATE);
    }

    public static Intent createGetIntent(final Context context, final long companionId, final long requestId) {
        if(context == null) {
            return null;
        }
        final Intent intent = BaseServiceTask.createBaseInputIntent(context, TYPE_GET, requestId);
        intent.putExtra(KEY_COMPANION, companionId);
        return intent;
    }

    public static Intent createUpdateIntent(final Context context, final long requestId) {
        if(context == null) {
            return null;
        }
        return BaseServiceTask.createBaseInputIntent(context, TYPE_UPDATE, requestId);
    }

    public static Intent startTask(final Context context, final Intent intent) {
        if (intent == null || context == null || !AuthManager.isAuth()) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        if(!NetworkConnectionManager.isConnection(context)) {
            return createResultIntent(intent, AuthResult.Result.NO_INTERNET);
        }

        if(isGetIntent(intent)) {
            return startGetTask(context, intent);
        } else
        if(isUpdateIntent(intent)) {
            return startUpdateTask(context, intent);
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }

    public static Intent startGetTask(final Context context, final Intent intent) {
        if (intent == null || context == null || !AuthManager.isAuth()) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        final long companionId = intent.getLongExtra(KEY_COMPANION, NULL_ID);
        final Dialog dialog = DialogService.getCompanionDialog(context, companionId);
        if(dialog != null) {
            final Intent outIntent = createResultIntent(intent, AuthResult.Result.SUCCESS);
            outIntent.putExtra(KEY_DIALOG_ID, dialog.getId());
            return outIntent;
        }

        try {
            final DialogJson result =
                    GeoTwitterApp.getApi().getDialog(AuthManager.getAuthToken(), companionId).execute().body();
            if(result != null) {
                DialogService.addDialog(context, result);
                final Intent outIntent = createResultIntent(intent, AuthResult.Result.SUCCESS);
                outIntent.putExtra(KEY_DIALOG_ID, result.getId());
                return outIntent;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }

    public static Intent startUpdateTask(final Context context, final Intent intent) {
        if (intent == null || context == null || !AuthManager.isAuth()) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        try {
            final List<DialogJson> result =
                    GeoTwitterApp.getApi().getAllDialogs(AuthManager.getAuthToken()).execute().body();
            if(result != null) {
                DialogService.addDialogs(context, result);
                return createResultIntent(intent, startUpdateDialogs(context));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }

    private static AuthResult.Result startUpdateDialogs(final Context context) {
        if(context == null) {
            return AuthResult.Result.FAIL;
        }
        final List<Dialog> dialogList = DialogService.getEmptyDialogList(context);
        if(dialogList == null) {
            return AuthResult.Result.FAIL;
        }
        for(Dialog dialog : dialogList) {
            try {
                final DialogJson dialogJson = GeoTwitterApp.getApi()
                        .getDialog(AuthManager.getAuthToken(), dialog.getCompanionId()).execute().body();
                DialogService.addDialog(context, dialogJson);
            }
            catch (Exception ex) {
                return AuthResult.Result.FAIL;
            }
        }
        return AuthResult.Result.SUCCESS;
    }

    public static long getDialogId(final Intent intent) {
        if(intent == null) {
            return NULL_ID;
        }
        return intent.getLongExtra(KEY_DIALOG_ID, NULL_ID);
    }
}
