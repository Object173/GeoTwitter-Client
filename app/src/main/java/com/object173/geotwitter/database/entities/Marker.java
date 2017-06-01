package com.object173.geotwitter.database.entities;


import android.content.ContentValues;
import android.database.Cursor;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.server.json.MarkerJson;

import java.io.Serializable;

public final class Marker implements Serializable {

    private long id;
    private double latitude;
    private double longitude;

    public Marker() {
    }

    public Marker(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Marker newInstance(final MarkerJson markerJson) {
        if(markerJson == null) {
            return  null;
        }
        return new Marker(markerJson.getLatitude(), markerJson.getLongitude());
    }

    public Marker(final Cursor cursor) {
        if(cursor == null) {
            return;
        }

        id = cursor.getLong(0);
        latitude = cursor.getDouble(1);
        longitude = cursor.getDouble(2);
    }

    public ContentValues insert() {
        final ContentValues values = new ContentValues();
        values.put(DatabaseContract.MarkerTableScheme.COLUMN_LATITUDE, latitude);
        values.put(DatabaseContract.MarkerTableScheme.COLUMN_LONGITUDE, longitude);
        return values;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
