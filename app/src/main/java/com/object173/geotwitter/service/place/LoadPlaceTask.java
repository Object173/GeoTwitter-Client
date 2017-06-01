package com.object173.geotwitter.service.place;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.object173.geotwitter.GeoTwitterApp;
import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.NewPlace;
import com.object173.geotwitter.database.service.PlaceService;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.server.json.Filter;
import com.object173.geotwitter.server.json.NewPlaceJson;
import com.object173.geotwitter.service.BaseServiceTask;
import com.object173.geotwitter.util.NetworkConnectionManager;
import com.object173.geotwitter.util.PreferencesManager;
import com.object173.geotwitter.util.user.AuthManager;

import java.util.List;

public final class LoadPlaceTask extends BaseServiceTask {

    private static final String TYPE_LOAD_TOP = "place_load_top";
    private static final String TYPE_LOAD_BOTTOM = "place_load_bottom";

    private static final String KEY_AUTHOR_ID = "place_id";
    private static final String KEY_FILTER = "filter";

    public static boolean isThisIntent(final Intent intent) {
        return isLoadTopIntent(intent) || isLoadBottomIntent(intent);
    }

    public static boolean isLoadTopIntent(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_LOAD_TOP);
    }

    public static boolean isLoadBottomIntent(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_LOAD_BOTTOM);
    }

    public static Intent createLoadTopIntent(final Context context, final long authorId, final long requestId) {
        if(context == null) {
            return null;
        }
        final Intent intent = BaseServiceTask.createBaseInputIntent(context, TYPE_LOAD_TOP, requestId);
        intent.putExtra(KEY_AUTHOR_ID, authorId);
        return intent;
    }

    public static Intent createLoadBottomIntent(final Context context, final long authorId,
                                                final Filter filter, final long requestId) {
        if(context == null || filter == null) {
            return null;
        }
        final Intent intent = BaseServiceTask.createBaseInputIntent(context, TYPE_LOAD_BOTTOM, requestId);
        intent.putExtra(KEY_AUTHOR_ID, authorId);
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

        if(isLoadTopIntent(intent)) {
            return startLoadTopTask(context, intent);
        } else
        if(isLoadBottomIntent(intent)) {
            return startLoadBottomTask(context, intent);
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }

    private static Intent startLoadTopTask(final Context context, final Intent intent) {
        if (intent == null || context == null || !AuthManager.isAuth()) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        final long authorId = intent.getExtras().getLong(KEY_AUTHOR_ID, NULL_ID);

        try {
            final List<NewPlaceJson> result;
            if(authorId <= NULL_ID) {
                final NewPlace place = PlaceService.getLastPlace(context);
                final long lastId = (place != null) ? place.getId() : NULL_ID;
                result = GeoTwitterApp.getApi()
                        .getTopPlaces(AuthManager.getAuthToken(), lastId)
                        .execute().body();
            }
            else {
                final NewPlace place = PlaceService.getLastPlace(context, authorId);
                final long lastId = (place != null) ? place.getId() : NULL_ID;

                result = GeoTwitterApp.getApi()
                        .getTopPlaces(AuthManager.getAuthToken(), authorId, lastId)
                        .execute().body();
            }
            if(result != null) {
                Log.d("AddPlace", "SUCCESS + " + result.size());
                PlaceService.addNewPlace(context, result);

                final int listSize = PreferencesManager.getIntPreference(context,
                        context.getString(R.string.key_preference_list_size),
                        context.getResources().getInteger(R.integer.preference_list_size_default));

                PlaceService.cropPlaceList(context, listSize);
                return createResultIntent(intent, AuthResult.Result.SUCCESS);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("AddPlace", "result null");
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }

    private static Intent startLoadBottomTask(final Context context, final Intent intent) {
        if (intent == null || context == null || !AuthManager.isAuth()) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        final long authorId = intent.getExtras().getLong(KEY_AUTHOR_ID);
        final Filter filter = (Filter) intent.getExtras().getSerializable(KEY_FILTER);

        if(filter == null) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        try {
            final List<NewPlaceJson> result;
            if(authorId <= NULL_ID) {
                result = GeoTwitterApp.getApi()
                        .getBottomPlaces(AuthManager.getAuthToken(), filter)
                        .execute().body();
            }
            else {
                result = GeoTwitterApp.getApi()
                        .getBottomPlaces(AuthManager.getAuthToken(), authorId, filter)
                        .execute().body();
            }
            if(result != null) {
                Log.d("AddPlace", "SUCCESS");
                PlaceService.addNewPlace(context, result);
                return createResultIntent(intent, AuthResult.Result.SUCCESS);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("AddPlace", "result null");
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }

}
