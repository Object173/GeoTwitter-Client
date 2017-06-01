package com.object173.geotwitter.gui.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.object173.geotwitter.R;
import com.object173.geotwitter.util.PreferencesManager;

public final class MapHelper {

    private static boolean IS_GESTURES = true;
    private static boolean IS_COMPASS = true;
    private static boolean IS_MY_LOCATION = true;
    private static boolean IS_MY_LOCATION_BUTTON = true;
    private static boolean IS_ZOOM_CONTROLS = true;

    private static final String KEY_MAP_SPUTNIK = "map_sputnik_enabled";
    private GoogleMap currentMap;
    private boolean isSputnik = false;

    public final void onCreate(final SupportMapFragment mapFragment,
                               final OnMapReadyCallback onMapReadyCallback, final Bundle savedInstanceState) {
        if (mapFragment == null || onMapReadyCallback == null) {
            return;
        }
        mapFragment.getMapAsync(onMapReadyCallback);

        if(savedInstanceState != null) {
            isSputnik = savedInstanceState.getBoolean(KEY_MAP_SPUTNIK);
        }
        else {
            isSputnik = PreferencesManager.isMapSputnik(mapFragment.getContext());
        }
    }

    public final void onSaveInstanceState(final Bundle bundle) {
        if(bundle != null) {
            bundle.putBoolean(KEY_MAP_SPUTNIK, isSputnik);
        }
    }

    public static void initMap(final Context context, final GoogleMap googleMap) {
        if (googleMap == null || context == null) {
            return;
        }

        final UiSettings uiSettings = googleMap.getUiSettings();
        if (uiSettings == null) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(IS_MY_LOCATION);
            uiSettings.setMyLocationButtonEnabled(IS_MY_LOCATION_BUTTON);
        }

        uiSettings.setAllGesturesEnabled(IS_GESTURES);
        uiSettings.setCompassEnabled(IS_COMPASS);

        googleMap.setMapType(getMapType(context));

        final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm != null) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (location != null) {
                final LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                final CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(position)
                        .zoom(context.getResources().getInteger(R.integer.simple_map_default_zoom))
                        .build();
                final CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                googleMap.moveCamera(cameraUpdate);
            }
        }
    }

    public static Location getCurrentLocation(final Context context) {
        if(context == null) {
            return null;
        }
        final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm != null) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            return location;
        }
        return null;
    }

    public final void onStop(final Context context) {
        if(context != null && currentMap != null) {
            PreferencesManager.setCameraPosition(context, currentMap.getCameraPosition());
            PreferencesManager.setBooleanPreference(context,
                    context.getString(R.string.key_preference_enabled_sputnik), isSputnik);
        }
    }

    public final boolean isMapSputnik(final Context context) {
        return isSputnik;
    }

    public static int getMapType(final Context context) {
        if(PreferencesManager.getBooleanPreference(context,
                context.getString(R.string.key_preference_enabled_sputnik),
                context.getResources().getBoolean(R.bool.preference_enabled_sputnik_default))) {
            return GoogleMap.MAP_TYPE_HYBRID;
        }
        else {
            return GoogleMap.MAP_TYPE_NORMAL;
        }
    }
}
