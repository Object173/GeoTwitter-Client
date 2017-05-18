package com.object173.geotwitter.gui.main;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
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
import com.object173.geotwitter.gui.contacts.AddContactActivity;
import com.object173.geotwitter.gui.options.OptionsMenuActivity;
import com.object173.geotwitter.service.authorization.AuthAccount;

import java.io.IOException;

public class MainActivity extends MyBaseActivity implements TabLayout.OnTabSelectedListener {

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!super.onCreate(savedInstanceState, R.layout.activity_main, false)) {
            finish();
            return;
        }

        verificationAccount();

        fab = (FloatingActionButton) findViewById(R.id.fab_button);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        final MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(),
                new String[]{getString(R.string.dialogs_list_fragment_title),
                        getString(R.string.contacts_list_fragment_title)});

        viewPager.setAdapter(pagerAdapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);

        setFabState(viewPager.getCurrentItem());
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
            case R.id.action_options:
                startActivity(new Intent(this, OptionsMenuActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setFabState(final int position) {
        switch (position) {
            case 0:
                fab.setImageResource(R.mipmap.ic_message_white);
                fab.setOnClickListener(null);
                break;
            case 1:
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

    private void verificationAccount() {
        final AccountManager am = AccountManager.get(this);
        if (am.getAccountsByType(AuthAccount.TYPE).length == 0) {
            am.addAccount(AuthAccount.TYPE, AuthAccount.TOKEN_FULL_ACCESS, null, null, this,
                    new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                            try {
                                future.getResult();
                            } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                                e.printStackTrace();
                                MainActivity.this.finish();
                            }
                        }
                    }, null
            );
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
}
