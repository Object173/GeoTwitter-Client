package com.object173.geotwitter.database.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.entities.Image;

import java.util.ArrayList;
import java.util.List;

public final class ImageRepository {

    private static final int NULL_ID = DatabaseContract.NULL_ID;

    public static long insert(final SQLiteDatabase db, final Image image) {
        if(db == null || image == null) {
            return NULL_ID;
        }
        return db.insert(DatabaseContract.ImageTableScheme.TABLE_NAME, null, image.insert());
    }

    public static Image findByUrl(final SQLiteDatabase db, final String url) {
        if(db == null || url == null) {
            return null;
        }

        final Cursor cursor = db.query(DatabaseContract.ImageTableScheme.TABLE_NAME, null,
                DatabaseContract.ImageTableScheme.COLUMN_ONLINE_URL + "=?", new String[] {url},
                null, null, null);

        if(cursor.moveToFirst()) {
            final Image image = new Image(cursor);
            cursor.close();
            return image;
        }
        return null;
    }

    public static Image select(final SQLiteDatabase db, final long id) {
        if(db == null || id <= NULL_ID) {
            return null;
        }

        final Cursor cursor = db.query(DatabaseContract.ImageTableScheme.TABLE_NAME, null,
                DatabaseContract.ImageTableScheme._ID + "=?", new String[] {Long.toString(id)},
                null, null, null);

        if(cursor.moveToFirst()) {
            final Image image = new Image(cursor);
            cursor.close();
            return image;
        }
        return null;
    }

    public static List<Image> getAll(final SQLiteDatabase db) {
        if(db == null) {
            return null;
        }

        final Cursor cursor = db.query(DatabaseContract.ImageTableScheme.TABLE_NAME, null, null, null, null, null, null);

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

    public static long update(final SQLiteDatabase db, final Image image) {
        if(db == null || image == null) {
            return NULL_ID;
        }

        final long result = db.update(DatabaseContract.ImageTableScheme.TABLE_NAME, image.insert(),
                DatabaseContract.ImageTableScheme._ID + " = ?",
                new String[] { String.valueOf(image.getId())});
        return result;
    }

    public static boolean remove(final SQLiteDatabase db, final long id) {
        if(db == null || id <= NULL_ID) {
            return false;
        }

        final int result = db.delete(DatabaseContract.ImageTableScheme.TABLE_NAME,
                DatabaseContract.ImageTableScheme._ID + " = ?",
                new String[] { String.valueOf(id) });

        return (result > NULL_ID);
    }
}
