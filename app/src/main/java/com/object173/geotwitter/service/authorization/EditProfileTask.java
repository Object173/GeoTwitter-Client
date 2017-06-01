package com.object173.geotwitter.service.authorization;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.object173.geotwitter.GeoTwitterApp;
import com.object173.geotwitter.server.ServerContract;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.service.BaseServiceTask;
import com.object173.geotwitter.util.NetworkConnectionManager;
import com.object173.geotwitter.util.resources.ImageUtils;
import com.object173.geotwitter.util.resources.ImagesManager;
import com.object173.geotwitter.util.user.AuthManager;
import com.object173.geotwitter.util.user.AvatarManager;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public final class EditProfileTask extends BaseServiceTask {

    private static final String TYPE_EDIT_USERNAME = "edit_username";
    private static final String TYPE_EDIT_STATUS = "edit_status";
    private static final String TYPE_EDIT_PASSWORD = "edit_password";
    private static final String TYPE_EDIT_AVATAR = "edit_avatar";
    private static final String TYPE_REMOVE_AVATAR = "remove_avatar";

    private static final String KEY_USERNAME = "username";
    private static final String KEY_STATUS= "status";
    private static final String KEY_OLD_PASSWORD = "old_password";
    private static final String KEY_NEW_PASSWORD = "new_password";
    private static final String KEY_AVATAR = "avatar";

    public static boolean isThisIntent(final Intent intent) {
        return isEditUsername(intent) || isEditStatus(intent) ||
                isEditPassword(intent) || isEditAvatar(intent) || isRemoveAvatar(intent);
    }

    public static boolean isEditUsername(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_EDIT_USERNAME);
    }

    public static boolean isEditStatus(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_EDIT_STATUS);
    }

    public static boolean isEditPassword(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_EDIT_PASSWORD);
    }

    public static boolean isEditAvatar(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_EDIT_AVATAR);
    }

    public static boolean isRemoveAvatar(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_REMOVE_AVATAR);
    }

    public static Intent createEditUsernameIntent(final Context context, final String username, final long requestId) {
        if(context == null || username == null) {
            return null;
        }

        final Intent intent = BaseServiceTask.createBaseInputIntent(context, TYPE_EDIT_USERNAME, requestId);
        intent.putExtra(KEY_USERNAME, username);
        return intent;
    }

    public static Intent createEditStatusIntent(final Context context, final String status, final long requestId) {
        if(context == null || status == null) {
            return null;
        }

        final Intent intent = BaseServiceTask.createBaseInputIntent(context, TYPE_EDIT_STATUS, requestId);
        intent.putExtra(KEY_STATUS, status);
        return intent;
    }

    public static Intent createEditPasswordIntent(final Context context, final String oldPassword,
                                           final String newPassword, final long requestId) {
        if(context == null || oldPassword == null || newPassword == null) {
            return null;
        }

        final Intent intent = BaseServiceTask.createBaseInputIntent(context, TYPE_EDIT_PASSWORD, requestId);
        intent.putExtra(KEY_NEW_PASSWORD, newPassword);
        intent.putExtra(KEY_OLD_PASSWORD, oldPassword);
        return intent;
    }

    public static Intent createEditAvatarIntent(final Context context, final String avatar, final long requestId) {
        if(context == null || avatar == null) {
            return null;
        }

        final Intent intent = BaseServiceTask.createBaseInputIntent(context, TYPE_EDIT_AVATAR, requestId);
        intent.putExtra(KEY_AVATAR, avatar);
        return intent;
    }

    public static Intent createRemoveAvatarIntent(final Context context, final long requestId) {
        if(context == null) {
            return null;
        }

        return BaseServiceTask.createBaseInputIntent(context, TYPE_REMOVE_AVATAR, requestId);
    }

    public static Intent startTask(final Context context, final Intent intent) {
        Log.d("EditProfileTask", "start");
        if (intent == null || context == null || !AuthManager.isAuth()) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        if(!NetworkConnectionManager.isConnection(context)) {
            return createResultIntent(intent, AuthResult.Result.NO_INTERNET);
        }

        if(isEditUsername(intent)) {
            return startEditUsername(context, intent);
        } else
        if(isEditStatus(intent)) {
            return startEditStatus(context, intent);
        } else
        if(isEditPassword(intent)) {
            return startEditPassword(intent);
        }
        if(isEditAvatar(intent)) {
            return startEditAvatar(context, intent);
        } else
        if(isRemoveAvatar(intent)) {
            return startRemoveAvatar(context, intent);
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }

    private static Intent startEditUsername(final Context context, final Intent intent) {
        final String username = intent.getStringExtra(KEY_USERNAME);

        try {
            final AuthResult.Result result =
                    GeoTwitterApp.getApi().setUsername(AuthManager.getAuthToken(), username).execute().body();
            if(result == AuthResult.Result.SUCCESS) {
                AuthManager.setUsername(context, username);
            }
            return createResultIntent(intent, result);
        } catch (Exception e) {
            e.printStackTrace();
            return createResultIntent(intent, AuthResult.Result.FAIL);
        }
    }

    private static Intent startEditStatus(final Context context, final Intent intent) {
        final String status = intent.getStringExtra(KEY_STATUS);

        try {
            final AuthResult.Result result =
                    GeoTwitterApp.getApi().setStatus(AuthManager.getAuthToken(), status).execute().body();
            if(result == AuthResult.Result.SUCCESS) {
                AuthManager.setStatus(context, status);
            }
            return createResultIntent(intent, result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }

    private static Intent startEditPassword(final Intent intent) {
        final String oldPassword = intent.getStringExtra(KEY_OLD_PASSWORD);
        final String newPassword = intent.getStringExtra(KEY_NEW_PASSWORD);

        try {
            final AuthResult.Result result =
                    GeoTwitterApp.getApi().setPassword(AuthManager.getAuthToken(), oldPassword, newPassword)
                            .execute().body();
            return createResultIntent(intent, result);
        } catch (Exception e) {
            e.printStackTrace();
            return createResultIntent(intent, AuthResult.Result.FAIL);
        }
    }

    private static Intent startEditAvatar(final Context context, final Intent intent) {
        final String avatarPath = intent.getStringExtra(KEY_AVATAR);

        final Bitmap avatar = ImageUtils.resizeBitmap(
                ImagesManager.readImage(avatarPath), ImageUtils.LARGE_WIDTH);
        ImagesManager.writeImage(context, avatar, avatarPath);

        final MultipartBody.Part avatarPart;

        final File file = new File(avatarPath);
        if(file.exists()) {
            RequestBody avatarFile = RequestBody.create(MediaType.parse(ServerContract.MEDIA_TYPE_FILE), file);
            avatarPart = MultipartBody.Part.createFormData(
                    ServerContract.ATTR_AVATAR_NAME, file.getName(), avatarFile);
        }
        else {
            avatarPart = null;
        }

        try {
            final String result =
                    GeoTwitterApp.getApi().setAvatar(AuthManager.getAuthToken(), avatarPart)
                            .execute().body();
            if(result != null) {
                AvatarManager.writeAvatar(context, avatarPath);
                AuthManager.setAvatar(context, result);
                return createResultIntent(intent, AuthResult.Result.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }

    private static Intent startRemoveAvatar(final Context context, final Intent intent) {
        try {
            final AuthResult.Result result =
                    GeoTwitterApp.getApi().removeAvatar(AuthManager.getAuthToken()).execute().body();
            if(result == AuthResult.Result.SUCCESS) {
                AuthManager.setAvatar(context, null);
            }
            return createResultIntent(intent, result);
        } catch (Exception e) {
            e.printStackTrace();
            return createResultIntent(intent, AuthResult.Result.FAIL);
        }
    }
}
