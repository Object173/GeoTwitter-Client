package com.object173.geotwitter.database.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.server.json.DialogJson;

import java.io.Serializable;

public final class Dialog implements Serializable{

    private long id;
    private long companionId;

    private Profile companion;

    public Dialog() {
    }

    public Dialog(final DialogJson dialogJson) {
        if(dialogJson != null) {
            this.id = dialogJson.getId();
            if(dialogJson.getCompanion() != null) {
                this.companionId = dialogJson.getCompanion().getUserId();
            }
        }
    }

    public Dialog(final Cursor cursor) {
        if(cursor == null) {
            return;
        }

        id = cursor.getLong(0);
        companionId = cursor.getLong(1);
    }

    public ContentValues insert() {
        final ContentValues values = new ContentValues();
        values.put(DatabaseContract.DialogsTableScheme._ID, id);
        values.put(DatabaseContract.DialogsTableScheme.COLUMN_COMPANION, companionId);
        return values;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCompanionId() {
        return companionId;
    }

    public void setCompanionId(long companionId) {
        this.companionId = companionId;
    }

    public Profile getCompanion() {
        return companion;
    }

    public void setCompanion(Profile companion) {
        this.companion = companion;
    }
}
