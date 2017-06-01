package com.object173.geotwitter.util.resources;

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

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.object173.geotwitter.database.entities.Marker;
import com.object173.geotwitter.gui.util.DeleteImageActivity;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class ChooserManager {

    public static final int INTENT_PICK_IMAGE = 173;
    public static final int MARKER_PICKER_REQUEST = 102;

    private static final String IMAGE_NAME = "image_";
    private static int imageId = 0;

    public static void selectImage(final Activity activity, final Uri uri, final String path) {
        if(activity == null || uri == null || path == null) {
            return;
        }
        final File outFile = new File(path);
        new Crop(uri).output(Uri.fromFile(outFile)).asSquare().start(activity);
    }

    private static List<Intent> getCameraIntent(final Activity activity) {
        if(activity == null) {
            return null;
        }
        final Uri outputFileUri = createCameraImageUri();

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
        return cameraIntents;
    }

    private static Intent getGalleryIntent() {
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        return galleryIntent;
    }

    public static void showImageChooser(final Activity activity, final String title, final String cachePath) {
        if(activity == null || title == null) {
            return;
        }

        final List<Intent> cameraIntents = getCameraIntent(activity);

        if(cachePath != null) {
            cameraIntents.add(DeleteImageActivity.getStartIntent(activity, cachePath));
        }

        final Intent galleryIntent = getGalleryIntent();

        final Intent chooserIntent = Intent.createChooser(galleryIntent, title);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        activity.startActivityForResult(chooserIntent, INTENT_PICK_IMAGE);
    }

    public static void showGalleryChooser(final Activity activity, final String title) {
        if(activity == null || title == null) {
            return;
        }
        final Intent galleryIntent = getGalleryIntent();
        final Intent chooserIntent = Intent.createChooser(galleryIntent, title);
        activity.startActivityForResult(chooserIntent, INTENT_PICK_IMAGE);
    }

    public static void showCameraChooser(final Activity activity) {
        if(activity == null) {
            return;
        }
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, createCameraImageUri());
        activity.startActivityForResult(intent, INTENT_PICK_IMAGE);
    }

    private static Uri createCameraImageUri() {
        imageId ++;
        return getCameraImageUri();
    }

    private static Uri getCameraImageUri() {
        final File tmpFile = new File(Environment.getExternalStorageDirectory(),
                IMAGE_NAME + String.valueOf(imageId));
        return Uri.fromFile(tmpFile);
    }

    public static Uri getImageUri(final Context context, final Intent data) {
        if(context == null) {
            return null;
        }
        if(data == null || data.getData() == null) {
            return getCameraImageUri();
        }
        return data.getData();
    }

    public static boolean showMarkerChooser(final Activity activity) {
        if(activity == null) {
            return false;
        }
        final PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            activity.startActivityForResult(builder.build(activity), MARKER_PICKER_REQUEST);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Marker getMarker(final Context context, final Intent intent) {
        if(context == null || intent == null) {
            return null;
        }
        final Place place = PlacePicker.getPlace(context, intent);
        if(place == null) {
            return null;
        }
        final LatLng position = place.getLatLng();
        if (position == null) {
            return null;
        }
        return new Marker(position.latitude, position.longitude);
    }
}
