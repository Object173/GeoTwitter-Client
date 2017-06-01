package com.object173.geotwitter.database.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.entities.Image;
import com.object173.geotwitter.database.entities.NewPlace;
import com.object173.geotwitter.util.user.AuthManager;

import java.util.ArrayList;
import java.util.List;

public final class PlaceRepository {

    private static final int NULL_ID = DatabaseContract.NULL_ID;

    public static NewPlace findById(final SQLiteDatabase db, final long id) {
        if(db == null) {
            return null;
        }
        final Cursor cursor = db.query(DatabaseContract.PlaceTableScheme.TABLE_NAME, null,
                DatabaseContract.ImageTableScheme._ID + " = ? ",
                new String[] {String.valueOf(id)}, null, null, null);

        return select(db, cursor);
    }

    private static NewPlace select(final SQLiteDatabase db, final Cursor cursor) {
        if(db != null && cursor != null && cursor.moveToFirst()) {
            final NewPlace place = new NewPlace(cursor);
            place.setMarker(MarkerRepository.findById(db, place.getMarkerId()));
            cursor.close();
            return place;
        }
        return null;
    }

    public static List<Image> getPlaceImages(final SQLiteDatabase db, final long id) {
        if(db == null) {
            return null;
        }
        final Cursor cursor = db.query(DatabaseContract.ImageTableScheme.TABLE_NAME, null,
                DatabaseContract.ImageTableScheme._ID + " IN ( select " +
                DatabaseContract.PlaceImageTableScheme.COLUMN_IMAGE + " from " +
                DatabaseContract.PlaceImageTableScheme.TABLE_NAME + " where " +
                DatabaseContract.PlaceImageTableScheme.COLUMN_PLACE + " = ? )",
                new String[] {String.valueOf(id)}, null, null, null);

        if(cursor.moveToFirst()) {
            final List<Image> imageList = new ArrayList<>();
            do {
                imageList.add(new Image(cursor));
            }
            while (cursor.moveToNext());
            cursor.close();
            return imageList;
        }
        return null;
    }

    public static boolean insertImageList(final SQLiteDatabase db, final List<Image> imageList, final long placeId) {
        if(db == null || imageList == null || imageList.size() <= 0) {
            return false;
        }
        for(Image image : imageList) {
            final long imageId = ImageRepository.insert(db, image);
            db.insert(DatabaseContract.PlaceImageTableScheme.TABLE_NAME, null, NewPlace.insertImage(placeId, imageId));
        }
        return true;
    }

    public static List<Image> selectImageList(final SQLiteDatabase db, final long placeId) {
        if(db == null || placeId <= NULL_ID) {
            return null;
        }
        final Cursor cursor = db.query(DatabaseContract.PlaceImageTableScheme.TABLE_NAME, null,
                DatabaseContract.PlaceImageTableScheme.COLUMN_PLACE + " = ?",
                new String[] {String.valueOf(placeId)}, null, null, null);

        if(!cursor.moveToFirst()) {
            return null;
        }

        final List<Image> imageList = new ArrayList<>();
        do {
            final long imageId = NewPlace.getImageId(cursor);
            imageList.add(ImageRepository.select(db, imageId));
        }
        while (cursor.moveToNext());

        return imageList;
    }

    public static NewPlace findLastByAuthor(final SQLiteDatabase db, final long authorId) {
        if(db == null || authorId <= NULL_ID) {
            return null;
        }

        final Cursor cursor = db.query(DatabaseContract.PlaceTableScheme.TABLE_NAME, null,
                DatabaseContract.PlaceTableScheme.COLUMN_AUTHOR + " = ?",
                new String[] {String.valueOf(authorId)}, null, null,
                DatabaseContract.PlaceTableScheme.COLUMN_DATE + " DESC LIMIT 1");

        return select(db, cursor);
    }

    public static NewPlace findLast(final SQLiteDatabase db) {
        if(db == null) {
            return null;
        }

        final Cursor cursor = db.query(DatabaseContract.PlaceTableScheme.TABLE_NAME, null,
                DatabaseContract.PlaceTableScheme.COLUMN_AUTHOR + " <> ?",
                new String[] {String.valueOf(AuthManager.getAuthToken().getUserId())}, null, null,
                DatabaseContract.PlaceTableScheme.COLUMN_DATE + " DESC LIMIT 1");

        return select(db, cursor);
    }

}
