package com.object173.geotwitter.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.soundcloud.android.crop.Crop;

import java.io.File;

/**
 * Created by Object173
 * on 28.04.2017.
 */

public final class AvatarManager {

    private static final String FILENAME_AVATAR = "user_avatar.png";

    public static void selectAvatar(final Activity activity, final Uri uri) {
        if(activity == null || uri == null) {
            return;
        }

        try {
            final Cursor cursor = activity.getContentResolver().query(uri,
                    new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
            cursor.moveToFirst();
            cursor.close();
        }
        catch (NullPointerException ex) {
        }

        final File inFile = new File(activity.getCacheDir(), FILENAME_AVATAR);

        final File outFile = new File(activity.getFilesDir(), FILENAME_AVATAR);
        new Crop(uri).output(Uri.fromFile(outFile)).asSquare().start(activity);
    }

    public static String getAvatarPath(final Context context) {
        if(context == null) {
            return null;
        }
        final File avatarFile = new File(context.getFilesDir(), FILENAME_AVATAR);
        return avatarFile.getAbsolutePath();
    }

    public static Uri getAvatarUri(final Context context) {
        if(context == null) {
            return null;
        }
        final File avatarFile = new File(context.getFilesDir(), FILENAME_AVATAR);
        return Uri.parse(avatarFile.getAbsolutePath());
    }

    public static boolean isAvatarExist(final Context context) {
        return context != null && new File(getAvatarPath(context)).exists();
    }

}
