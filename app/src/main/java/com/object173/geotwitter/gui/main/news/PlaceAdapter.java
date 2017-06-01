package com.object173.geotwitter.gui.main.news;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.Image;
import com.object173.geotwitter.database.entities.NewPlace;
import com.object173.geotwitter.database.entities.Profile;
import com.object173.geotwitter.database.service.PlaceService;
import com.object173.geotwitter.gui.base.CursorRecyclerViewAdapter;
import com.object173.geotwitter.gui.images.ImageViewerActivity;
import com.object173.geotwitter.gui.views.CircleImageView;
import com.object173.geotwitter.util.PreferencesManager;
import com.object173.geotwitter.util.resources.ImagesManager;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

public final class PlaceAdapter extends CursorRecyclerViewAdapter<PlaceAdapter.ViewHolder>
        implements LocationListener {

    private final OnItemClickListener listener;
    private final Context context;

    private Location location = null;

    public PlaceAdapter(final Context context, final Cursor cursor, final OnItemClickListener listener) {
        super(cursor);
        this.listener = listener;
        this.context = context;
    }

    public final void onResume() {

        if(!PreferencesManager.getBooleanPreference(context,
                context.getString(R.string.key_preference_enabled_location), false)) {
            return;
        }

        final int locationTimer = PreferencesManager.getIntPreference(context,
                context.getString(R.string.key_preference_location_timer),
                context.getResources().getInteger(R.integer.preference_location_timer_default)) * 1000;

        final int locationDistance = PreferencesManager.getIntPreference(context,
                context.getString(R.string.key_preference_location_distance),
                context.getResources().getInteger(R.integer.preference_location_distance_default));

        final LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if(locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locationTimer, locationDistance, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, locationTimer, locationDistance, this);

            onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        }
    }

    public final void onPause() {
        final LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if(locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_place_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.bind(cursor, listener);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        notifyDataSetChanged();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    public interface OnItemClickListener {
        void onProfileClick(Profile item);
        void onPlaceClick(NewPlace item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final LinearLayout authorLayout;
        final ImageView avatarView;
        final TextView usernameView;
        final TextView dateView;
        final TextView distanceView;

        final CardView cardView;
        final ImageView placeImageView;
        final TextView titleView;
        final TextView bodyView;

        ViewHolder(final View view) {
            super(view);

            authorLayout = (LinearLayout) view.findViewById(R.id.author_layout);

            avatarView = (CircleImageView) view.findViewById(R.id.avatar_view);
            usernameView = (TextView) view.findViewById(R.id.username_field);
            dateView = (TextView) view.findViewById(R.id.date_field);
            distanceView = (TextView) view.findViewById(R.id.distance_field);

            cardView = (CardView) view.findViewById(R.id.card_view);

            placeImageView = (ImageView) view.findViewById(R.id.place_image);
            titleView = (TextView) view.findViewById(R.id.place_title_field);
            bodyView = (TextView) view.findViewById(R.id.place_body_field);
        }

        void bind(final Cursor cursor, final OnItemClickListener listener) {
            final NewPlace place = PlaceService.getPlace(context, cursor);
            final Profile profile = place.getAuthor();

            if(profile.getAvatarMini() != null) {
                ImagesManager.setImageViewCache(context, avatarView, R.mipmap.avatar,
                        profile.getAvatarMini());
            }
            else {
                avatarView.setImageResource(R.mipmap.avatar);
            }
            usernameView.setText(profile.getUsername());
            dateView.setText(place.getDateTimeString());

            if(location == null || place.getMarker() == null) {
                distanceView.setVisibility(View.GONE);
            }
            else {
                distanceView.setVisibility(View.VISIBLE);
                final String distance = getDistanceString(context,
                        calculationDistanceByCoord(location.getLatitude(), location.getLongitude(),
                                place.getMarker().getLatitude(), place.getMarker().getLongitude()));
                if(distance != null) {
                    distanceView.setText(distance);
                }
                else {
                    distanceView.setText("");
                }
            }

            if(place.getImages() != null && place.getImages().size() > 0) {
                placeImageView.setVisibility(View.VISIBLE);
                ImagesManager.setImageViewOnline(context, placeImageView, R.mipmap.ic_image,
                        place.getImages().get(0).getOnlineUrl());

                placeImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImageViewerActivity.startActivity(context, (ArrayList<Image>) place.getImages(), 0, place.getTitle());
                    }
                });
            }
            else {
                placeImageView.setVisibility(View.GONE);
            }

            titleView.setText(place.getTitle());
            bodyView.setText(place.getBody());

            authorLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        listener.onProfileClick(profile);
                    }
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onPlaceClick(place);
                    }
                }
            });
        }
    }

    private static String getDistanceString(final Context context, final double dist) {
        if(context == null) {
            return null;
        }
        if(dist < 1000) {
            return String.valueOf((int) dist) + context.getString(R.string.place_distance_m);
        } else
        if(dist < 10000) {
            final String formattedDouble = String.format("%.2f", dist / 1000);
            return formattedDouble + context.getString(R.string.place_distance_km);
        } else
        if(dist < 100000) {
            final String formattedDouble = String.format("%.1f", dist / 1000);
            return formattedDouble + context.getString(R.string.place_distance_km);
        }
        return String.valueOf((int) dist / 1000) + context.getString(R.string.place_distance_km);
    }

    private static double calculationDistanceByCoord(double startPointLat,double startPointLon,double endPointLat,double endPointLon){
        float[] results = new float[1];
        Location.distanceBetween(startPointLat, startPointLon, endPointLat, endPointLon, results);
        return results[0];
    }
}
