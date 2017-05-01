package com.object173.geotwitter.map;

import android.content.SharedPreferences;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Object173
 * on 24.04.2017.
 */

public final class CameraState {

    private static final String KEY_LATITUDE = "camera_latitude";
    private static final String KEY_LONGITUDE = "camera_longitude";
    private static final String KEY_BEARING = "camera_bearing";
    private static final String KEY_TILT = "camera_tilt";
    private static final String KEY_ZOOM = "camera_zoom";

    private final static float NULL_POINT = 0;

    private final double latitude;
    private final double longitude;

    private final float bearing;
    private final float tilt;
    private final float zoom;

    public CameraState(final CameraPosition cameraPosition) {
        if(cameraPosition == null) {
            throw new NullPointerException();
        }
        this.latitude = cameraPosition.target.latitude;
        this.longitude = cameraPosition.target.longitude;
        this.bearing = cameraPosition.bearing;
        this.tilt = cameraPosition.tilt;
        this.zoom = cameraPosition.zoom;
    }

    public CameraState(final SharedPreferences preferences) {
        if(preferences == null) {
            throw new NullPointerException();
        }
        this.latitude = preferences.getFloat(KEY_LATITUDE, NULL_POINT);
        this.longitude = preferences.getFloat(KEY_LONGITUDE, NULL_POINT);
        this.bearing = preferences.getFloat(KEY_BEARING, NULL_POINT);
        this.tilt = preferences.getFloat(KEY_TILT, NULL_POINT);
        this.zoom = preferences.getFloat(KEY_ZOOM, NULL_POINT);
    }

    public final CameraPosition getCameraPosition() {
        return new CameraPosition(new LatLng(latitude, longitude), zoom, tilt, bearing);
    }

    public final void saveState(final SharedPreferences.Editor editor) {
        if(editor == null) {
            return;
        }
        editor.putFloat(KEY_LATITUDE, (float)latitude);
        editor.putFloat(KEY_LONGITUDE, (float)longitude);
        editor.putFloat(KEY_BEARING, bearing);
        editor.putFloat(KEY_TILT, tilt);
        editor.putFloat(KEY_ZOOM, zoom);
    }
}
