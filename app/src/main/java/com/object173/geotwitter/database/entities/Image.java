package com.object173.geotwitter.database.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.object173.geotwitter.database.DatabaseContract;

import java.io.Serializable;

public final class Image implements Serializable{
    private long id;
    private String localPath;
    private String onlineUrl;

    public Image(int id, String localPath, String onlineUrl) {
        this.id = id;
        this.localPath = localPath;
        this.onlineUrl = onlineUrl;
    }

    public Image(String localPath, String onlineUrl) {
        this(DatabaseContract.NULL_ID, localPath, onlineUrl);
    }

    public Image(final Cursor cursor) {
        if(cursor == null) {
            return;
        }

        id = cursor.getLong(0);
        localPath = cursor.getString(1);
        onlineUrl = cursor.getString(2);
    }

    public static Image newLocalImage(final String path) {
        return new Image(path, null);
    }

    public static Image newNetworkImage(final String path) {
        return new Image(null, path);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getOnlineUrl() {
        return onlineUrl;
    }

    public void setOnlineUrl(String onlineUrl) {
        this.onlineUrl = onlineUrl;
    }

    public ContentValues insert() {
        final ContentValues values = new ContentValues();
        if(localPath != null) {
            values.put(DatabaseContract.ImageTableScheme.COLUMN_LOCAL_PATH, localPath);
        }
        values.put(DatabaseContract.ImageTableScheme.COLUMN_ONLINE_URL, onlineUrl);
        return values;
    }
}
