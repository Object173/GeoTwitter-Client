package com.object173.geotwitter.database.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.server.json.MessageJson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Message {

    private long localId;
    private long globalId;

    private long senderId;
    private long dialogId;

    private String text;

    private long imageId;
    private Image image;

    private long markerId;
    private Marker marker;

    private Date date;
    private int status;

    public static final int STATUS_UNREAD = 1;
    public static final int STATUS_WAIT= 2;
    public static final int STATUS_SUCCESS = 3;
    public static final int STATUS_FAIL = 4;

    public Message() {
    }

    public Message(long dialogId, long senderId, String text, Date date, int status) {
        this.senderId = senderId;
        this.dialogId = dialogId;
        this.text = text;
        this.date = date;
        this.status = status;
    }

    public Message(final MessageJson messageJson, final int status) {
        if(messageJson != null) {
            this.globalId = messageJson.getId();
            this.senderId = messageJson.getSenderId();
            this.dialogId = messageJson.getDialogId();
            this.text = messageJson.getText();
            if(messageJson.getImageUrl() != null) {
                this.image = Image.newNetworkImage(messageJson.getImageUrl());
            }
            if(messageJson.getMarker() != null) {
                this.marker = Marker.newInstance(messageJson.getMarker());
            }
            this.date = messageJson.getDate();
            this.status = status;
        }
    }

    public Message(final MessageJson messageJson, final boolean isNewMessage) {
        this(messageJson, STATUS_SUCCESS);
        if(isNewMessage) {
            this.status = STATUS_UNREAD;
        }
    }

    public Message(final Cursor cursor) {
        if(cursor == null) {
            return;
        }

        localId = cursor.getLong(0);
        globalId = cursor.getLong(1);
        senderId = cursor.getLong(2);
        dialogId = cursor.getLong(3);
        text = cursor.getString(4);
        imageId = cursor.getLong(5);
        markerId = cursor.getLong(6);
        date = new Date(cursor.getLong(7));
        status = cursor.getInt(8);
    }

    public ContentValues insert() {
        final ContentValues values = new ContentValues();
        values.put(DatabaseContract.MessagesTableScheme.COLUMN_GLOBAL_ID, globalId);
        values.put(DatabaseContract.MessagesTableScheme.COLUMN_SENDER, senderId);
        values.put(DatabaseContract.MessagesTableScheme.COLUMN_DIALOG, dialogId);
        values.put(DatabaseContract.MessagesTableScheme.COLUMN_TEXT, text);
        values.put(DatabaseContract.MessagesTableScheme.COLUMN_MARKER, markerId);
        values.put(DatabaseContract.MessagesTableScheme.COLUMN_IMAGE, imageId);
        values.put(DatabaseContract.MessagesTableScheme.COLUMN_DATE, date.getTime());
        values.put(DatabaseContract.MessagesTableScheme.COLUMN_STATUS, status);
        return values;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public long getGlobalId() {
        return globalId;
    }

    public void setGlobalId(long globalId) {
        this.globalId = globalId;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getDialogId() {
        return dialogId;
    }

    public void setDialogId(long dialogId) {
        this.dialogId = dialogId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public long getMarkerId() {
        return markerId;
    }

    public void setMarkerId(long markerId) {
        this.markerId = markerId;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public final String getDateTimeString() {
        if(date == null) {
            return null;
        }
        final DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        return dateFormat.format(date);
    }

    public final String getDateString() {
        if(date == null) {
            return null;
        }
        final DateFormat dateFormat = SimpleDateFormat.getDateInstance();
        return dateFormat.format(date);
    }
}
