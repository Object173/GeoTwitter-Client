package com.object173.geotwitter.util.resources;

import android.content.Context;
import android.util.Log;

import com.object173.geotwitter.database.DatabaseController;
import com.object173.geotwitter.service.contacts.ContactsService;
import com.object173.geotwitter.service.messenger.MessengerService;

import java.io.File;


public final class CacheManager {

    private static String CACHE_PATH = null;

    private static final String SEPARATOR = "/";
    private static final String RESOURCES_PATH = SEPARATOR + "resources/";
    private static final String IMAGES_PATH = RESOURCES_PATH + "images/";

    private static final String IMAGE_FORMAT = ".png";

    public static void onCreate(final Context context) {
        if(context != null) {
            CACHE_PATH = context.getFilesDir().getAbsolutePath();
            createNonExistsDirectory(IMAGES_PATH);
        }
    }

    public static boolean clearCache(final Context context) {
        if (context == null) {
            return false;
        }
        final File resources = new File(CACHE_PATH + RESOURCES_PATH);

        removeDirectory(resources);
        ImagesManager.invalidateAll(context);

        onCreate(context);
        DatabaseController.dropDatabase(context);
        return true;
    }

    private static void removeDirectory(final File directory) {
        if(directory == null || !directory.exists() || !directory.isDirectory()) {
            return;
        }
        if (directory.isDirectory()) {
            final File[] children = directory.listFiles();
            for (File file : children) {
                if(file.isDirectory()) {
                    removeDirectory(file);
                }
                else {
                    Log.d("remove","path " + file.getAbsolutePath() + " " + file.delete());
                }
            }
        }
        Log.d("remove","path " + directory.getAbsolutePath() + " " + directory.delete());
    }

    public static String getImagesPath() {
        return CACHE_PATH + IMAGES_PATH;
    }

    public static String createImagePath(final long id) {
        return CACHE_PATH + IMAGES_PATH + String.valueOf(id) + IMAGE_FORMAT;
    }

    public static String getImagePath(final String imageName) {
        if(imageName == null) {
            return null;
        }
        return new File(getImagesPath(), imageName).getPath();
    }

    private static boolean createNonExistsDirectory(final String path) {
        if (path == null) {
            return false;
        }
        final File directory = new File(CACHE_PATH + path);
        return directory.exists() || directory.mkdirs();
    }

    public static void syncData(final Context context) {
        if(context != null) {
            ContactsService.startToUpdateContacts(context);
            MessengerService.startToUpdateDialogs(context);
        }
    }
}
