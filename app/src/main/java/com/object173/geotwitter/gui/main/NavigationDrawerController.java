package com.object173.geotwitter.gui.main;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.gui.options.OptionsActivity;
import com.object173.geotwitter.gui.views.CircleImageView;
import com.object173.geotwitter.util.LoginManager;

/**
 * Created by Object173
 * on 26.04.2017.
 */

final class NavigationDrawerController {
    private DrawerLayout drawerLayout = null;

    final void onCreate(final MyBaseActivity activity,
                        final NavigationView.OnNavigationItemSelectedListener navigationListener) {
        if(activity == null || navigationListener == null) {
            return;
        }
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawerLayout, activity.getToolbar(),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navigationListener);

        final MyMapFragment mapFragment =
                (MyMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.map_fragment);

        if(mapFragment != null && mapFragment.isMapSputnik(activity)) {
            navigationView.getMenu().findItem(R.id.nav_menu_map_sputnik).setChecked(true);
        }
        else {
            navigationView.getMenu().findItem(R.id.nav_menu_map_default).setChecked(true);
        }

        final CircleImageView avatar = (CircleImageView)navigationView.getHeaderView(0)
                .findViewById(R.id.image_view_avatar);
    }

    final boolean onNavigationItemSelected(final Activity activity, final MyMapFragment mapFragment,
                                           final MenuItem item) {
        if(activity == null || mapFragment == null || item == null) {
            return false;
        }

        switch (item.getItemId()) {
            case R.id.nav_menu_map_default:
                if(!item.isChecked()) {
                    mapFragment.setMapSputnik(false);
                }
                break;
            case R.id.nav_menu_map_sputnik:
                if(!item.isChecked()) {
                    mapFragment.setMapSputnik(true);
                }
                break;
            case R.id.nav_menu_options:
                final Intent intent = new Intent(activity, OptionsActivity.class);
                activity.startActivity(intent);
                break;
            case R.id.nav_menu_logout:
                LoginManager.logout(activity);
                break;
        }

        if(drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

}
