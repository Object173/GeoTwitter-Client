package com.object173.geotwitter.database;

import android.content.Context;

public final class DatabaseController {

    public static void dropDatabase(final Context context) {
        if(context == null) {
            return;
        }
        try {
            final DatabaseHelper dbHelper = new DatabaseHelper(context);
            dbHelper.getWritableDatabase().delete(DatabaseContract.ImageTableScheme.TABLE_NAME, null, null);
            dbHelper.getWritableDatabase().delete(DatabaseContract.ProfileTableScheme.TABLE_NAME, null, null);
            dbHelper.getWritableDatabase().delete(DatabaseContract.DialogsTableScheme.TABLE_NAME, null, null);
            dbHelper.getWritableDatabase().delete(DatabaseContract.MessagesTableScheme.TABLE_NAME, null, null);
            dbHelper.getWritableDatabase().delete(DatabaseContract.MarkerTableScheme.TABLE_NAME, null, null);
            dbHelper.getWritableDatabase().delete(DatabaseContract.PlaceTableScheme.TABLE_NAME, null, null);
            dbHelper.getWritableDatabase().delete(DatabaseContract.PlaceImageTableScheme.TABLE_NAME, null, null);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
