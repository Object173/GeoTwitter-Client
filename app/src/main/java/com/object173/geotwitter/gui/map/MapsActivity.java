package com.object173.geotwitter.gui.map;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.Marker;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.gui.place.AddPlaceActivity;
import com.object173.geotwitter.gui.util.MapHelper;
import com.object173.geotwitter.server.json.NewPlaceJson;

import java.util.List;

public class MapsActivity extends MyBaseActivity
        implements SearchView.OnQueryTextListener {

    private static final int LOADER_ID = 19;
    private MapFragment mapFragment = null;

    private String searchString = null;
    private static final String KEY_SEARCH = "search";

    public static final String DISTANCE_BAR_KEY = "distance";
    public static final String PERIOD_BAR_KEY = "period";

    private SeekBarController distanceBar = new SeekBarController(DISTANCE_BAR_KEY, mapFragment);
    private SeekBarController periodBar = new SeekBarController(PERIOD_BAR_KEY, mapFragment);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!super.onCreate(savedInstanceState, R.layout.activity_maps, true)) {
            finish();
            return;
        }

        if(savedInstanceState != null) {
            searchString = savedInstanceState.getString(KEY_SEARCH);
        }

        mapFragment = (MapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        final SeekBar distanceSeekBar = (SeekBar) findViewById(R.id.distance_seekbar);
        final TextView distanceField = (TextView) findViewById(R.id.distance_field);
        distanceBar.onCreate(savedInstanceState, distanceSeekBar, distanceField,
                getResources().getInteger(R.integer.activity_map_distance_default),
                getResources().getInteger(R.integer.activity_map_distance_min),
                getResources().getInteger(R.integer.activity_map_distance_max));
        distanceBar.setListener(mapFragment);

        final SeekBar periodSeekBar = (SeekBar) findViewById(R.id.period_seekbar);
        final TextView periodField = (TextView) findViewById(R.id.period_field);
        periodBar.onCreate(savedInstanceState, periodSeekBar, periodField,
                getResources().getInteger(R.integer.activity_map_period_default),
                getResources().getInteger(R.integer.activity_map_period_min),
                getResources().getInteger(R.integer.activity_map_period_max));

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, AddPlaceActivity.class));
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        distanceBar.onSaveInstanceState(outState);
        periodBar.onSaveInstanceState(outState);

        outState.putString(KEY_SEARCH, searchString);
    }

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        if (searchString != null && !searchString.isEmpty()) {
            searchItem.expandActionView();
            searchView.setQuery(searchString, true);
            searchView.clearFocus();
        }
        searchView.setOnQueryTextListener(this);

        final EditText searchPlate = (EditText) searchView.findViewById(R.id.search_src_text);
        searchPlate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(v.getText().toString());
                }
                return false;
            }
        });

        return true;
    }

    public void search(final String query) {
        searchString = query;
        final Location location = MapHelper.getCurrentLocation(this);
        if(location != null) {
            final Marker marker = new Marker(location.getLatitude(), location.getLongitude());
            final Loader<List<NewPlaceJson>> loader = getSupportLoaderManager().restartLoader(LOADER_ID,
                    PlacesLoader.newInstance(marker, searchString, distanceBar.getProgress(), periodBar.getProgress()), mapFragment);
            loader.forceLoad();
        }
        else {
            showSnackbar(R.string.activity_map_message_fail_location, false);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchString = newText;
        return true;
    }
}
