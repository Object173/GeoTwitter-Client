package com.object173.geotwitter.database.service;

import android.content.Context;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.DatabaseHelper;
import com.object173.geotwitter.database.entities.Image;
import com.object173.geotwitter.database.repository.ImageRepository;
import com.object173.geotwitter.util.resources.CacheManager;
import com.object173.geotwitter.util.resources.ImagesManager;

import java.util.List;

public final class ImageService {

    private static final long NULL_ID = DatabaseContract.NULL_ID;

    public static Image getImage(final Context context, final long id) {
        if(context == null) {
            return null;
        }
        try {
            final DatabaseHelper db = new DatabaseHelper(context);
            final Image result = ImageRepository.select(db.getReadableDatabase(), id);
            db.close();
            return result;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static long addImage(final Context context, final Image image) {
        if(context == null || image == null) {
            return NULL_ID;
        }
        try {
            final DatabaseHelper db = new DatabaseHelper(context);
            final Image oldImage = ImageRepository.findByUrl(db.getReadableDatabase(), image.getOnlineUrl());

            final long imageId;
            if(oldImage == null) {
                imageId = ImageRepository.insert(db.getWritableDatabase(), image);
                if (imageId <= NULL_ID) {
                    return NULL_ID;
                }
            }
            else {
                imageId = oldImage.getId();
            }

            image.setId(imageId);
            image.setLocalPath(CacheManager.createImagePath(imageId));
            ImageRepository.update(db.getWritableDatabase(), image);

            db.close();
            return imageId;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return NULL_ID;
        }
    }

    public static boolean removeImage(final Context context, final long id) {
        if(context == null) {
            return false;
        }
        try {
            final DatabaseHelper db = new DatabaseHelper(context);
            final Image image = ImageRepository.select(db.getReadableDatabase(), id);
            if(image == null) {
                return false;
            }
            ImagesManager.deleteImage(image.getLocalPath());
            ImageRepository.remove(db.getWritableDatabase(), id);

            db.close();
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    public static List<Image> getAllImages(final Context context) {
        if(context == null) {
            return null;
        }
        try {
            final DatabaseHelper db = new DatabaseHelper(context);
            final List<Image> imageList = ImageRepository.getAll(db.getReadableDatabase());
            db.close();
            return imageList;
        }
        catch (Exception ex) {
            return null;
        }
    }
}
