package com.object173.geotwitter.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class MyContentProvider extends ContentProvider {

    static final String AUTHORITY = "com.object173.geotwitter.contacts";
    static final String CONTACT_PATH = DatabaseContract.ProfileTableScheme.TABLE_NAME;
    static final String DIALOGS_PATH = DatabaseContract.DialogsTableScheme.TABLE_NAME;
    static final String MESSAGES_PATH = DatabaseContract.MessagesTableScheme.TABLE_NAME;
    static final String PLACES_PATH = DatabaseContract.PlaceTableScheme.TABLE_NAME;

    public static final Uri CONTACT_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + CONTACT_PATH);

    static final String CONTACT_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + CONTACT_PATH;

    public static final Uri DIALOG_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + DIALOGS_PATH);

    static final String DIALOGS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + DIALOGS_PATH;


    public static final Uri MESSAGES_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + MESSAGES_PATH);

    static final String MESSAGES_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + MESSAGES_PATH;

    public static final Uri PLACES_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + PLACES_PATH);

    static final String PLACES_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + PLACES_PATH;


    static final int URI_CONTACTS = 1;
    static final int URI_DIALOGS = 2;
    static final int URI_MESSAGES = 3;
    static final int URI_PLACES = 4;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CONTACT_PATH, URI_CONTACTS);
        uriMatcher.addURI(AUTHORITY, DIALOGS_PATH, URI_DIALOGS);
        uriMatcher.addURI(AUTHORITY, MESSAGES_PATH, URI_MESSAGES);
        uriMatcher.addURI(AUTHORITY, PLACES_PATH, URI_PLACES);
    }

    DatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        final Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case URI_CONTACTS:
                cursor = db.query(DatabaseContract.ProfileTableScheme.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), CONTACT_CONTENT_URI);
                break;
            case URI_DIALOGS:
                cursor = db.query(DatabaseContract.DialogsTableScheme.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), DIALOG_CONTENT_URI);
                break;
            case URI_MESSAGES:
                cursor = db.query(DatabaseContract.MessagesTableScheme.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), MESSAGES_CONTENT_URI);
                break;
            case URI_PLACES:
                cursor = db.query(DatabaseContract.PlaceTableScheme.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), PLACES_CONTENT_URI);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_CONTACTS:
                return CONTACT_CONTENT_TYPE;
            case URI_DIALOGS:
                return DIALOGS_CONTENT_TYPE;
            case URI_MESSAGES:
                return MESSAGES_CONTENT_TYPE;
            case URI_PLACES:
                return PLACES_CONTENT_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowID;
        final Uri resultUri;
        switch (uriMatcher.match(uri)) {
            case URI_CONTACTS:
                rowID = db.insert(DatabaseContract.ProfileTableScheme.TABLE_NAME, null, contentValues);
                resultUri = ContentUris.withAppendedId(CONTACT_CONTENT_URI, rowID);
                break;
            case URI_DIALOGS:
                rowID = db.insert(DatabaseContract.DialogsTableScheme.TABLE_NAME, null, contentValues);
                resultUri = ContentUris.withAppendedId(DIALOG_CONTENT_URI, rowID);
                break;
            case URI_MESSAGES:
                rowID = db.insert(DatabaseContract.MessagesTableScheme.TABLE_NAME, null, contentValues);
                resultUri = ContentUris.withAppendedId(MESSAGES_CONTENT_URI, rowID);
                break;
            case URI_PLACES:
                rowID = db.insert(DatabaseContract.PlaceTableScheme.TABLE_NAME, null, contentValues);
                resultUri = ContentUris.withAppendedId(PLACES_CONTENT_URI, rowID);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int cnt;
        switch (uriMatcher.match(uri)) {
            case URI_CONTACTS:
                cnt = db.delete(DatabaseContract.ProfileTableScheme.TABLE_NAME, selection, selectionArgs);
                break;
            case URI_DIALOGS:
                cnt = db.delete(DatabaseContract.DialogsTableScheme.TABLE_NAME, selection, selectionArgs);
                break;
            case URI_MESSAGES:
                cnt = db.delete(DatabaseContract.MessagesTableScheme.TABLE_NAME, selection, selectionArgs);
                break;
            case URI_PLACES:
                cnt = db.delete(DatabaseContract.PlaceTableScheme.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int cnt;
        switch (uriMatcher.match(uri)) {
            case URI_CONTACTS:
                cnt = db.update(DatabaseContract.ProfileTableScheme.TABLE_NAME, values, selection, selectionArgs);
                break;
            case URI_DIALOGS:
                cnt = db.update(DatabaseContract.DialogsTableScheme.TABLE_NAME, values, selection, selectionArgs);
                break;
            case URI_MESSAGES:
                cnt = db.update(DatabaseContract.MessagesTableScheme.TABLE_NAME, values, selection, selectionArgs);
                break;
            case URI_PLACES:
                cnt = db.update(DatabaseContract.PlaceTableScheme.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }
}
