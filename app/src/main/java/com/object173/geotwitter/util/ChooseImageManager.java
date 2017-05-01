package com.object173.geotwitter.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;

import com.object173.geotwitter.gui.DeleteAvatarActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Object173
 * on 28.04.2017.
 */

public final class ChooseImageManager {

    private static final String IMAGE_NAME = "local_image";

    public static void showImageChooser(final Activity activity, final String title,
                                        final int REQUEST_CODE) {
        if(activity == null || title == null) {
            return;
        }

        final Uri outputFileUri = getCameraImageUri();

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = activity.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }
        cameraIntents.add(DeleteAvatarActivity.getStartIntent(activity, AvatarManager.getAvatarPath(activity)));

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, title);

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        activity.startActivityForResult(chooserIntent, REQUEST_CODE);
    }

    private static Uri getCameraImageUri() {
        final File tmpFile = new File(Environment.getExternalStorageDirectory(), IMAGE_NAME);
        return Uri.fromFile(tmpFile);
    }

    public static Uri getImageUri(final Context context, final Intent data) {
        if(context == null) {
            return null;
        }

        final boolean isCamera;
        if(data == null) {
            isCamera = true;
        }
        else {
            final String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        if(isCamera) {
            return getCameraImageUri();
        }
        else {
            return data.getData();
        }
    }
}
