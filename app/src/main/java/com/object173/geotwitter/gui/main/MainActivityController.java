package com.object173.geotwitter.gui.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.View;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.base.MyBaseActivity;

/**
 * Created by Object173
 * on 24.04.2017.
 */

final class MainActivityController{

    private MyMapFragment mapFragment = null;
    private final NavigationDrawerController navController = new NavigationDrawerController();

    final void onCreate(final MyBaseActivity activity,
                        final NavigationView.OnNavigationItemSelectedListener navigationListener,
                        final Bundle savedInstanceState) {
        if(activity == null || navigationListener == null) {
            return;
        }

        mapFragment =
                (MyMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.map_fragment);

        final FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        navController.onCreate(activity, navigationListener);
    }

    final void onStop(final Context context) {
        if(context == null) {
            return;
        }
    }

    final boolean onNavigationItemSelected(final Activity activity, final MenuItem item) {
        return activity != null && item != null &&
                navController.onNavigationItemSelected(activity, mapFragment, item);
    }
}
