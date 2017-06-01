package com.object173.geotwitter.database.repository;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.entities.Marker;

public final class MarkerRepository {

    private static final int NULL_ID = DatabaseContract.NULL_ID;

    public static long insert(final SQLiteDatabase db, final Marker marker) {
        if(db == null || marker == null) {
            return NULL_ID;
        }
        return db.insert(DatabaseContract.MarkerTableScheme.TABLE_NAME, null, marker.insert());
    }

    public static Marker findById(final SQLiteDatabase db, final long id) {
        if(db == null) {
            return null;
        }
        final Cursor cursor = db.query(DatabaseContract.MarkerTableScheme.TABLE_NAME, null,
                DatabaseContract.MarkerTableScheme._ID + "=?", new String[] {String.valueOf(id)},
                null, null, null);

        return select(cursor);
    }

    public static Marker select(final Cursor cursor) {
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        final Marker marker = new Marker(cursor);
        cursor.close();
        return marker;
    }

    public static boolean remove(final SQLiteDatabase db, final long id) {
        if(db == null || id <= NULL_ID) {
            return false;
        }

        final int result = db.delete(DatabaseContract.MarkerTableScheme.TABLE_NAME,
                DatabaseContract.ImageTableScheme._ID + " = ?",
                new String[] { String.valueOf(id) });

        return (result > NULL_ID);
    }
}
