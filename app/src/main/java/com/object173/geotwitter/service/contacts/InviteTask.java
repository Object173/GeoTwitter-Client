package com.object173.geotwitter.service.contacts;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.object173.geotwitter.GeoTwitterApp;
import com.object173.geotwitter.database.service.PlaceService;
import com.object173.geotwitter.database.service.ProfileService;
import com.object173.geotwitter.server.json.AuthProfile;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.service.BaseServiceTask;
import com.object173.geotwitter.util.NetworkConnectionManager;
import com.object173.geotwitter.util.user.AuthManager;

public final class InviteTask extends BaseServiceTask {

    private static final String TYPE_ADD = "add_invite";
    private static final String TYPE_REMOVE = "remove_invite";

    private static final String KEY_CONTACT_ID = "new_profile";
    private static final String KEY_CONTACT_RELATION = "new_relation";

    public static boolean isThisIntent(final Intent intent) {
        return isAddInvite(intent) || isRemoveInvite(intent);
    }

    public static boolean isAddInvite(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_ADD);
    }

    public static boolean isRemoveInvite(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_REMOVE);
    }

    public static Intent createAddInviteIntent(final Context context, final long contactId, final long requestId) {
        if(context == null) {
            return null;
        }

        final Intent intent = BaseServiceTask.createBaseInputIntent(context, TYPE_ADD, requestId);
        intent.putExtra(KEY_CONTACT_ID, contactId);
        return intent;
    }

    public static Intent createRemoveInviteIntent(final Context context, final long contactId, final long requestId) {
        if(context == null) {
            return null;
        }

        final Intent intent = BaseServiceTask.createBaseInputIntent(context, TYPE_REMOVE, requestId);
        intent.putExtra(KEY_CONTACT_ID, contactId);
        return intent;
    }

    public static Intent startTask(final Context context, final Intent intent) {
        if (intent == null || context == null || !AuthManager.isAuth()) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        if(!NetworkConnectionManager.isConnection(context)) {
            return createResultIntent(intent, AuthResult.Result.NO_INTERNET);
        }

        if(isAddInvite(intent)) {
            return startAddInvite(context, intent);
        } else
        if(isRemoveInvite(intent)) {
            return startRemoveInvite(context, intent);
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }

    private static Intent startAddInvite(final Context context, final Intent intent) {
        final long contactId = intent.getLongExtra(KEY_CONTACT_ID, NULL_ID);
        Log.d("startAddInvite", ""+contactId);
        try {
            final AuthProfile result =
                    GeoTwitterApp.getApi().sendInvite(AuthManager.getAuthToken(), contactId).execute().body();
            if(result != null) {
                ProfileService.addProfile(context, result);
                return createSuccessIntent(intent, result.getUserId(), result.getRelation());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }

    private static Intent startRemoveInvite(final Context context, final Intent intent) {
        final long contactId = intent.getLongExtra(KEY_CONTACT_ID, NULL_ID);

        try {
            final AuthProfile result =
                    GeoTwitterApp.getApi().removeInvite(AuthManager.getAuthToken(), contactId).execute().body();
            if(result != null) {
                ProfileService.addProfile(context, result);
                PlaceService.removeByAuthor(context, result.getUserId());
                return createSuccessIntent(intent, result.getUserId(), result.getRelation());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }

    private static Intent createSuccessIntent(final Intent intent, final long userId,
                                              final AuthProfile.RelationStatus relation) {
        final Intent outIntent = createResultIntent(intent, AuthResult.Result.SUCCESS);
        outIntent.putExtra(KEY_CONTACT_ID, userId);
        outIntent.putExtra(KEY_CONTACT_RELATION, relation);
        return outIntent;
    }

    public static long getContactId(final Intent intent) {
        if(intent != null) {
            return intent.getLongExtra(KEY_CONTACT_ID, NULL_ID);
        }
        return NULL_ID;
    }

    public static AuthProfile.RelationStatus getRelation(final Intent intent) {
        if(intent != null) {
            return (AuthProfile.RelationStatus) intent.getSerializableExtra(KEY_CONTACT_RELATION);
        }
        return null;
    }
}
