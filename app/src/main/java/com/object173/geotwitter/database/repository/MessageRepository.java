package com.object173.geotwitter.database.repository;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.entities.Message;

public final class MessageRepository {

    private static final long NULL_ID = DatabaseContract.NULL_ID;

    private static Message select(final SQLiteDatabase db, final Cursor cursor) {
        if(db == null || cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        final Message message = new Message(cursor);
        message.setImage(ImageRepository.select(db, message.getImageId()));
        message.setMarker(MarkerRepository.findById(db, message.getMarkerId()));
        cursor.close();
        return message;
    }

    public static Message findLastByDialog(final SQLiteDatabase db, final long dialogId) {
        if(db == null || dialogId <= NULL_ID) {
            return null;
        }

        final Cursor cursor = db.query(DatabaseContract.MessagesTableScheme.TABLE_NAME, null,
                DatabaseContract.MessagesTableScheme.COLUMN_DIALOG + " = ? AND " +
                DatabaseContract.MessagesTableScheme.COLUMN_GLOBAL_ID + " > ?",
                new String[] {String.valueOf(dialogId), String.valueOf(NULL_ID)}, null, null,
                DatabaseContract.MessagesTableScheme.COLUMN_DATE + " DESC");

        return select(db, cursor);
    }

    public static Message findByLocalId(final SQLiteDatabase db, final long localId) {
        if(db == null || localId <= NULL_ID) {
            return null;
        }

        final Cursor cursor = db.query(DatabaseContract.MessagesTableScheme.TABLE_NAME, null,
                DatabaseContract.MessagesTableScheme._ID + "=?",
                new String[] {Long.toString(localId)}, null, null,
                DatabaseContract.MessagesTableScheme.COLUMN_DATE + " DESC");

        return select(db, cursor);
    }

    public static Message findByGlobalId(final SQLiteDatabase db, final long globalId) {
        if(db == null || globalId <= NULL_ID) {
            return null;
        }

        final Cursor cursor = db.query(DatabaseContract.MessagesTableScheme.TABLE_NAME, null,
                DatabaseContract.MessagesTableScheme.COLUMN_GLOBAL_ID + "=?",
                new String[] {Long.toString(globalId)}, null, null,
                DatabaseContract.MessagesTableScheme.COLUMN_DATE + " DESC");

        return select(db, cursor);
    }

    public static int getUnreadCount(final SQLiteDatabase db, final long dialogId) {
        if(db == null) {
            return (int)NULL_ID;
        }

        final Cursor cursor = db.query(DatabaseContract.MessagesTableScheme.TABLE_NAME, null,
                DatabaseContract.MessagesTableScheme.COLUMN_DIALOG + "=? AND " +
                DatabaseContract.MessagesTableScheme.COLUMN_STATUS + "=?",
                new String[] {Long.toString(dialogId), String.valueOf(Message.STATUS_UNREAD)},
                null, null, null);
        final int result  = cursor.getCount();
        cursor.close();
        return result;
    }

    public static boolean update(final SQLiteDatabase db, final Message message) {
        if(db == null || message == null) {
            return false;
        }

        return db.update(DatabaseContract.MessagesTableScheme.TABLE_NAME, message.insert(),
                DatabaseContract.MessagesTableScheme._ID + " = ?",
                new String[] { String.valueOf(message.getLocalId())}) > DatabaseContract.NULL_ID;
    }

    public static long insert(final SQLiteDatabase db, final Message message) {
        if(db == null || message == null) {
            return NULL_ID;
        }
        return db.insert(DatabaseContract.MessagesTableScheme.TABLE_NAME, null, message.insert());
    }
}
