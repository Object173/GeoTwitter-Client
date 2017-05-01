package com.object173.geotwitter.gui.main;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.object173.geotwitter.map.MapHelper;

/**
 * Created by Object173
 * on 24.04.2017.
 */

public final class MyMapFragment extends SupportMapFragment implements OnMapReadyCallback {
    private final MapHelper mapHelper = new MapHelper();

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapHelper.onCreate(this, this, savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public final void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        mapHelper.onSaveInstanceState(bundle);
    }

    @Override
    public final void onStop() {
        super.onStop();
        mapHelper.onStop(getContext());
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mapHelper.initMap(getContext(), googleMap);
    }

    public final void setMapSputnik(final boolean isSputnik) {
        mapHelper.setMapSputnik(isSputnik);
    }

    public final boolean isMapSputnik(final Context context) {
        return mapHelper.isMapSputnik(context);
    }
}
