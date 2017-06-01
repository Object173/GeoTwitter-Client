package com.object173.geotwitter.gui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.gui.choose.ChooseContactActivity;
import com.object173.geotwitter.gui.contacts.AddContactActivity;
import com.object173.geotwitter.gui.map.MapsActivity;
import com.object173.geotwitter.gui.options.AccountActivity;
import com.object173.geotwitter.gui.options.OptionsActivity;
import com.object173.geotwitter.gui.place.AddPlaceActivity;
import com.object173.geotwitter.service.messenger.MessengerService;
import com.object173.geotwitter.util.user.AuthManager;

public class MainActivity extends MyBaseActivity implements TabLayout.OnTabSelectedListener {

    private FloatingActionButton fab;
    private static final int VIEW_PAGER_OFFSCREEN = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!super.onCreate(savedInstanceState, R.layout.activity_main, true)) {
            finish();
            return;
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_mapmode);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab_button);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(VIEW_PAGER_OFFSCREEN);
        final MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(),
                new String[]{getString(R.string.places_list_fragment_title),
                        getString(R.string.dialogs_list_fragment_title),
                        getString(R.string.contacts_list_fragment_title)});

        viewPager.setAdapter(pagerAdapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);

        setFabState(viewPager.getCurrentItem());
    }

    @Override
    protected void onStart() {
        super.onStart();
        AuthManager.onStart(this);
    }

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item == null) {
            return false;
        }

        switch(item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, MapsActivity.class));
                return true;
            case R.id.action_options:
                startActivity(new Intent(this, OptionsActivity.class));
                return true;
            case R.id.action_profile:
                startActivity(new Intent(this, AccountActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setFabState(final int position) {
        switch (position) {
            case 0:
                fab.setImageResource(R.mipmap.ic_add_location_white);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getBaseContext(), AddPlaceActivity.class));
                    }
                });
                break;
            case 1:
                fab.setImageResource(R.mipmap.ic_message_white);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(new Intent(MainActivity.this, ChooseContactActivity.class),
                                ChooseContactActivity.REQUEST_CODE);
                    }
                });
                break;
            case 2:
                fab.setImageResource(R.mipmap.ic_add_contact_white);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, AddContactActivity.class));
                    }
                });
                break;
        }
    }

    @Override
    public void onTabSelected(final TabLayout.Tab tab) {
        if(tab != null) {
            setFabState(tab.getPosition());
        }
    }

    @Override
    public void onTabUnselected(final TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(final TabLayout.Tab tab) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ChooseContactActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            final long contactId = ChooseContactActivity.getContactId(data);
            MessengerService.startToGetDialog(MainActivity.this, contactId);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
