package com.object173.geotwitter.gui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.gui.login.LoginActivity;

public final class MainActivity extends MyBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final MainActivityController controller = new MainActivityController();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if(super.onCreate(savedInstanceState, R.layout.activity_main, null)) {
            controller.onCreate(this, this, savedInstanceState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        controller.onStop(this);
    }

    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public final boolean onNavigationItemSelected(final MenuItem item) {
        return controller.onNavigationItemSelected(this, item);
    }
}
