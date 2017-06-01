package com.object173.geotwitter.service.contacts;

import android.content.Context;
import android.content.Intent;

import com.object173.geotwitter.GeoTwitterApp;
import com.object173.geotwitter.database.service.ProfileService;
import com.object173.geotwitter.server.json.AuthProfile;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.service.BaseServiceTask;
import com.object173.geotwitter.util.NetworkConnectionManager;
import com.object173.geotwitter.util.user.AuthManager;

import java.util.List;

public final class UpdateContactsTask extends BaseServiceTask {

    private static final String TYPE = "update_contacts_list";

    public static boolean isThisIntent(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE);
    }

    public static Intent createUpdateIntent(final Context context, final long requestId) {
        if(context == null) {
            return null;
        }

        return BaseServiceTask.createBaseInputIntent(context, TYPE, requestId);
    }

    public static Intent startTask(final Context context, final Intent intent) {
        if (intent == null || context == null || !AuthManager.isAuth()) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        if(!NetworkConnectionManager.isConnection(context)) {
            return createResultIntent(intent, AuthResult.Result.NO_INTERNET);
        }

        try {
            final List<AuthProfile> result =
                    GeoTwitterApp.getApi().getAllContacts(AuthManager.getAuthToken()).execute().body();
            if(result != null) {
                ProfileService.addProfile(context, result);
                return createResultIntent(intent, AuthResult.Result.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }
}
