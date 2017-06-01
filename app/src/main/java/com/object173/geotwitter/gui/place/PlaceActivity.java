package com.object173.geotwitter.gui.place;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.Image;
import com.object173.geotwitter.database.entities.NewPlace;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.gui.images.ImageViewerActivity;
import com.object173.geotwitter.gui.util.MapHelper;
import com.object173.geotwitter.gui.util.WorkaroundMapFragment;
import com.object173.geotwitter.server.json.NewPlaceJson;
import com.object173.geotwitter.util.resources.ImagesManager;

import java.util.ArrayList;

public class PlaceActivity extends MyBaseActivity implements OnMapReadyCallback {

    private NewPlace place;

    private static final String KEY_PLACE = "place";

    public static boolean startActivity(final Context context, final NewPlace place) {
        if(context == null || place == null) {
            return false;
        }
        final Intent intent = new Intent(context, PlaceActivity.class);
        intent.putExtra(KEY_PLACE, place);
        context.startActivity(intent);
        return true;
    }

    public static boolean startActivity(final Context context, final NewPlaceJson place) {
        if(context == null || place == null) {
            return false;
        }
        final Intent intent = new Intent(context, PlaceActivity.class);
        intent.putExtra(KEY_PLACE, NewPlace.newInstance(place));
        context.startActivity(intent);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!super.onCreate(savedInstanceState, R.layout.activity_place, true)) {
            finish();
            return;
        }
        final NewPlace place = (NewPlace) getIntent().getSerializableExtra(KEY_PLACE);

        if(place == null) {
            finish();
            return;
        }

        initPlace(place);
    }

    private void initPlace(final NewPlace place) {
        if(place == null) {
            finish();
            return;
        }
        this.place = place;

        if(place.getImages() != null && place.getImages().size() > 0) {
            final ImageView imageView = (ImageView) findViewById(R.id.image_view);
            ImagesManager.setImageViewOnline(this, imageView, R.mipmap.ic_image,
                    place.getImages().get(0).getOnlineUrl());

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageViewerActivity.startActivity(PlaceActivity.this, (ArrayList<Image>) place.getImages(), 0,
                            place.getTitle());
                }
            });
        }

        final TextView titleView = (TextView) findViewById(R.id.title_field);
        titleView.setText(place.getTitle());
        final TextView bodyView = (TextView) findViewById(R.id.body_field);
        bodyView.setText(place.getBody());

        final NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.scroll_view);

        final WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        if(place.getMarker() != null) {
            mapFragment.getMapAsync(this);
        }
        else {
            getSupportFragmentManager().beginTransaction().hide(mapFragment).commit();
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        if(place == null || place.getMarker() == null) {
            return;
        }

        googleMap.setMapType(MapHelper.getMapType(this));
        googleMap.clear();

        final LatLng position = new LatLng(place.getMarker().getLatitude(), place.getMarker().getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(position));
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(getResources().getInteger(R.integer.simple_map_default_zoom))
                .build();
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.moveCamera(cameraUpdate);
    }
}
