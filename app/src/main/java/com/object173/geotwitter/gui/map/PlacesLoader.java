package com.object173.geotwitter.gui.map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import com.object173.geotwitter.GeoTwitterApp;
import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.Marker;
import com.object173.geotwitter.server.json.NewPlaceJson;
import com.object173.geotwitter.server.json.PlaceFilter;
import com.object173.geotwitter.util.user.AuthManager;

import java.util.List;

public final class PlacesLoader extends AsyncTaskLoader<List<NewPlaceJson>> {

    private Marker marker;
    private int distance;
    private int period;
    private String request;

    private static final String ARG_MARKER = "marker";
    private static final String ARG_DISTANCE = "distance";
    private static final String ARG_PERIOD = "period";
    private static final String ARG_REQUEST = "request";

    public static Bundle newInstance(final Marker marker, final String request, final int distance, final int period) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_MARKER, marker);
        bundle.putInt(ARG_DISTANCE, distance);
        bundle.putInt(ARG_PERIOD, period);
        bundle.putString(ARG_REQUEST, request);
        return bundle;
    }

    public PlacesLoader(final Context context, final Bundle args) {
        super(context);
        marker = (Marker) args.getSerializable(ARG_MARKER);
        distance = args.getInt(ARG_DISTANCE);
        period = args.getInt(ARG_PERIOD);
        request = args.getString(ARG_REQUEST);
    }

    @Override
    public List<NewPlaceJson> loadInBackground() {
        try {
            final PlaceFilter filter = new PlaceFilter(marker, request, distance, period);
            Log.d("MapPlaceLoader", filter.toString());
            final List<NewPlaceJson> result =
                    GeoTwitterApp.getApi().getAllPlaces(AuthManager.getAuthToken(), filter).execute().body();
            return result;
        }
        catch (Exception ex) {
            Toast.makeText(getContext(),R.string.login_activity_message_error_server, Toast.LENGTH_SHORT)
                    .show();
            ex.printStackTrace();
        }
        return null;
    }
}
