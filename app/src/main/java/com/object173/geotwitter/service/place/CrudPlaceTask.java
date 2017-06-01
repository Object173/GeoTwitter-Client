package com.object173.geotwitter.service.place;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.object173.geotwitter.GeoTwitterApp;
import com.object173.geotwitter.database.entities.Marker;
import com.object173.geotwitter.database.service.PlaceService;
import com.object173.geotwitter.server.ServerContract;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.server.json.MarkerJson;
import com.object173.geotwitter.server.json.NewPlaceJson;
import com.object173.geotwitter.service.BaseServiceTask;
import com.object173.geotwitter.util.NetworkConnectionManager;
import com.object173.geotwitter.util.resources.ImagesManager;
import com.object173.geotwitter.util.user.AuthManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public final class CrudPlaceTask extends BaseServiceTask {

    private static final String TYPE_ADD = "add_place";
    private static final String TYPE_RESEND = "resend_place";

    private static final String KEY_PLACE_ID = "place_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "body";
    private static final String KEY_MARKER = "marker";
    private static final String KEY_IMAGES = "images";

    public static boolean isThisIntent(final Intent intent) {
        return isAddIntent(intent) || isResendIntent(intent);
    }

    public static boolean isAddIntent(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_ADD);
    }

    public static boolean isResendIntent(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_RESEND);
    }

    public static Intent createAddIntent(final Context context, final String title, final String body,
                                         final Marker marker, final ArrayList<String> images, final long requestId) {
        if(context == null || title == null || body == null) {
            return null;
        }
        final Intent intent = BaseServiceTask.createBaseInputIntent(context, TYPE_ADD, requestId);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_BODY, body);
        intent.putExtra(KEY_MARKER, marker);
        intent.putExtra(KEY_IMAGES, images);
        return intent;
    }

    public static Intent createResendIntent(final Context context, final long placeId, final long requestId) {
        if(context == null) {
            return null;
        }
        final Intent intent = BaseServiceTask.createBaseInputIntent(context, TYPE_RESEND, requestId);
        intent.putExtra(KEY_PLACE_ID, placeId);
        return intent;
    }

    public static Intent startTask(final Context context, final Intent intent) {
        if (intent == null || context == null || !AuthManager.isAuth()) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        if(!NetworkConnectionManager.isConnection(context)) {
            return createResultIntent(intent, AuthResult.Result.NO_INTERNET);
        }

        if(isAddIntent(intent)) {
            return startAddTask(context, intent);
        } else
        if(isResendIntent(intent)) {
            return startResendTask(context, intent);
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }

    private static Intent startAddTask(final Context context, final Intent intent) {
        if (intent == null || context == null || !AuthManager.isAuth()) {
            Log.d("AddPlace", "NULL_POINTER");
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        final String title = intent.getExtras().getString(KEY_TITLE);
        final String body = intent.getExtras().getString(KEY_BODY);
        final Marker marker = (Marker) intent.getExtras().getSerializable(KEY_MARKER);
        final ArrayList<String> images = (ArrayList<String>) intent.getExtras().getSerializable(KEY_IMAGES);

        if(title == null || body == null) {
            Log.d("AddPlace", "NULL_POINTER");
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        final List<MultipartBody.Part> imageParts;
        if(images != null) {
            imageParts = new ArrayList<>();
            for (String imagePath : images) {
                final byte[] byteImage = ImagesManager.preparationImage(context, imagePath);
                if (byteImage != null) {
                    final RequestBody imageFile = RequestBody.create(MediaType.parse(ServerContract.MEDIA_TYPE_FILE), byteImage);
                    final MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                            ServerContract.ATTR_IMAGE_LIST_NAME, imagePath, imageFile);
                    imageParts.add(imagePart);
                }
            }
        }
        else {
            imageParts = null;
        }

        final NewPlaceJson newPlaceJson = new NewPlaceJson();
        newPlaceJson.setAuthorId(AuthManager.getAuthToken().getUserId());
        newPlaceJson.setTitle(title);
        newPlaceJson.setBody(body);
        newPlaceJson.setMarker(MarkerJson.newInstance(marker));

        try {
            final NewPlaceJson result = GeoTwitterApp.getApi()
                    .addPlace(AuthManager.getAuthToken(), newPlaceJson, imageParts)
                    .execute().body();
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

    private static Intent startResendTask(final Context context, final Intent intent) {
        if (intent == null || context == null || !AuthManager.isAuth()) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }
}
