package com.object173.geotwitter.map;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.object173.geotwitter.util.PreferencesManager;

/**
 * Created by Object173
 * on 24.04.2017.
 */

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

    public final void initMap(final Context context, final GoogleMap googleMap) {
        if (context != null || googleMap != null) {
            currentMap = googleMap;
            confUiMap(context);
            refreshMap(context);
        }
    }

    private void confUiMap(final Context context) {
        if (currentMap == null || context == null) {
            return;
        }

        final UiSettings uiSettings = currentMap.getUiSettings();
        if (uiSettings == null) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            currentMap.setMyLocationEnabled(IS_MY_LOCATION);
            uiSettings.setMyLocationButtonEnabled(IS_MY_LOCATION_BUTTON);
            return;
        }

        uiSettings.setAllGesturesEnabled(IS_GESTURES);
        uiSettings.setCompassEnabled(IS_COMPASS);
        uiSettings.setZoomControlsEnabled(IS_ZOOM_CONTROLS);
    }

    private void refreshMap(final Context context) {
        if(context == null || currentMap == null) {
            return;
        }

        final LatLng sydney = new LatLng(-34, 151);
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(sydney);
        markerOptions.title("Сидней");
        currentMap.addMarker(markerOptions);

        final CameraPosition position = PreferencesManager.getCameraPosition(context);
        if(position != null) {
            currentMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        }
        setMapSputnik(isSputnik);
    }

    public final void onStop(final Context context) {
        if(context != null && currentMap != null) {
            PreferencesManager.setCameraPosition(context, currentMap.getCameraPosition());
            PreferencesManager.setBooleanPreference(context, KEY_MAP_SPUTNIK, isSputnik);
        }
    }

    public final void setMapSputnik(final boolean isSputnik) {
        this.isSputnik = isSputnik;
        if(currentMap != null) {
            if(this.isSputnik) {
                currentMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
            else {
                currentMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }
    }

    public final boolean isMapSputnik(final Context context) {
        return isSputnik;
    }
}
