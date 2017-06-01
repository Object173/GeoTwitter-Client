package com.object173.geotwitter.database.repository;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.entities.Dialog;

import java.util.ArrayList;
import java.util.List;

public final class DialogRepository {

    private static final long NULL_ID = DatabaseContract.NULL_ID;

    private static Dialog select(final SQLiteDatabase db, final Cursor cursor) {
        if(db != null && cursor != null && cursor.moveToFirst()) {
            final Dialog dialog = new Dialog(cursor);
            dialog.setCompanion(ProfileRepository.select(db, dialog.getCompanionId()));
            cursor.close();
            return dialog;
        }
        return null;
    }

    public static Dialog findById(final SQLiteDatabase db, final long id) {
        if(db == null || id <= NULL_ID) {
            return null;
        }

        final Cursor cursor = db.query(DatabaseContract.DialogsTableScheme.TABLE_NAME, null,
                DatabaseContract.DialogsTableScheme._ID + "=?", new String[] {Long.toString(id)},
                null, null, null);

        return select(db, cursor);
    }

    public static Dialog findByCompanion(final SQLiteDatabase db, final long id) {
        if(db == null || id <= NULL_ID) {
            return null;
        }

        final Cursor cursor = db.query(DatabaseContract.DialogsTableScheme.TABLE_NAME, null,
                DatabaseContract.DialogsTableScheme.COLUMN_COMPANION + "=?",
                new String[] {Long.toString(id)},
                null, null, null);

        return select(db, cursor);
    }

    public static List<Dialog> getAllEmpty(final SQLiteDatabase db) {
        if(db == null) {
            return null;
        }

        final Cursor cursor = db.query(DatabaseContract.DialogsTableScheme.TABLE_NAME, null,
                "not exists (select * from " + DatabaseContract.MessagesTableScheme.TABLE_NAME + " where " +
                DatabaseContract.MessagesTableScheme.COLUMN_DIALOG + " = " +
                DatabaseContract.DialogsTableScheme.TABLE_NAME + "." + DatabaseContract.DialogsTableScheme._ID + ")",
                null, null, null, null);

        final List<Dialog> dialogList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                final Dialog dialog = new Dialog(cursor);
                dialog.setCompanion(ProfileRepository.select(db, dialog.getCompanionId()));
                dialogList.add(dialog);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return dialogList;
    }

}
