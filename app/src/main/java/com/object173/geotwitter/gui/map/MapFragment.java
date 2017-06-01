package com.object173.geotwitter.gui.map;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.place.PlaceActivity;
import com.object173.geotwitter.gui.util.MapHelper;
import com.object173.geotwitter.server.ServerContract;
import com.object173.geotwitter.server.json.NewPlaceJson;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public final class MapFragment extends SupportMapFragment
        implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<List<NewPlaceJson>>,
                    SeekBarController.OnChangeProgressListener{

    private GoogleMap mMap;
    private Circle loadRadius = null;
    private final PlaceInfoAdapter infoAdapter = new PlaceInfoAdapter();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MapHelper.initMap(getContext(), mMap);

        createLoadRadius(getContext().getResources().getInteger(R.integer.activity_map_distance_default) * 1000);
    }

    private void createLoadRadius(final int radius) {
        if(mMap == null) {
            return;
        }
        final Location currentLocation = MapHelper.getCurrentLocation(getContext());
        if(currentLocation != null) {
            final CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .radius(radius)
                    .strokeColor(getContext().getResources().getColor(R.color.color_radius_stroke))
                    .fillColor(getContext().getResources().getColor(R.color.color_radius_fill));
            loadRadius = mMap.addCircle(circleOptions);
        }
    }

    @Override
    public Loader<List<NewPlaceJson>> onCreateLoader(int id, Bundle args) {
        return new PlacesLoader(getContext(), args);
    }

    @Override
    public void onLoadFinished(Loader<List<NewPlaceJson>> loader, List<NewPlaceJson> data) {
        if(data != null && mMap != null) {
            infoAdapter.clearPlaces();
            mMap.clear();

            mMap.setInfoWindowAdapter(infoAdapter);
            mMap.setOnInfoWindowClickListener(infoAdapter);

            createLoadRadius((int)loadRadius.getRadius());

            for(NewPlaceJson place : data) {
                if(place.getMarker() == null) {
                    continue;
                }
                final LatLng position = new LatLng(place.getMarker().getLatitude(), place.getMarker().getLongitude());

                final Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(place.getTitle()));
                infoAdapter.addPlace(marker, place);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewPlaceJson>> loader) {
    }

    @Override
    public void setProgress(String key, int progress) {
        if(key.equals(MapsActivity.DISTANCE_BAR_KEY) && loadRadius != null) {
            loadRadius.setRadius(progress * 1000);
        }
    }

    private final class PlaceInfoAdapter
            implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener{

        private final HashMap<Marker, NewPlaceJson> eventMarkerMap = new HashMap<>();

        final void addPlace(final Marker marker, final NewPlaceJson place) {
            if(marker != null && place != null) {
                eventMarkerMap.put(marker, place);
            }
        }

        final void clearPlaces() {
            eventMarkerMap.clear();
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(final Marker marker) {
            final NewPlaceJson place = eventMarkerMap.get(marker);
            if(place == null || getContext() == null) {
                return null;
            }

            final View view = LayoutInflater.from(getContext()).inflate(R.layout.place_info_layout, null);

            final TextView titleField = (TextView) view.findViewById(R.id.title_field);
            final TextView dateField = (TextView) view.findViewById(R.id.date_field);
            final TextView bodyField = (TextView) view.findViewById(R.id.body_field);
            final ImageView imageView = (ImageView) view.findViewById(R.id.image_view);

            titleField.setText(place.getTitle());
            dateField.setText(place.getDateTimeString());

            if(place.getImages() != null && place.getImages().size() > 0) {
                Log.d("MapFragment",ServerContract.getAbsoluteUrl(place.getImages().get(0)));

                imageView.setVisibility(View.VISIBLE);

                try {
                    Picasso.with(getActivity())
                            .load(ServerContract.getAbsoluteUrl(place.getImages().get(0)))
                            .placeholder(R.mipmap.ic_image)
                            .error(R.mipmap.ic_image)
                            .into(imageView, new MarkerCallBack(marker, place.getImages().get(0), imageView));
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else {
                imageView.setVisibility(View.GONE);
            }

            bodyField.setText(place.getBody());

            return view;
        }

        @Override
        public void onInfoWindowClick(Marker marker) {
            if(marker == null || getContext() == null) {
                return;
            }
            final NewPlaceJson place = eventMarkerMap.get(marker);
            if(place != null) {
                PlaceActivity.startActivity(getContext(), place);
            }
        }
    }
}
