package com.object173.geotwitter.gui.map;

import android.widget.ImageView;

import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public final class MarkerCallBack implements Callback {
    private final Marker marker;
    private final String URL;
    private final ImageView imageView;


    MarkerCallBack(Marker marker, String URL, ImageView imageView) {
        this.marker  = marker;
        this.URL = URL;
        this.imageView = imageView;
    }

    @Override
    public void onError() {
    }

    @Override
    public void onSuccess() {
        if (marker != null && marker.isInfoWindowShown()) {
            marker.hideInfoWindow();

            Picasso.with(imageView.getContext())
                    .load(URL)
                    .fit()
                    .into(imageView);

            marker.showInfoWindow();
        }
    }
}
