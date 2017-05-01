package com.object173.geotwitter.gui.base;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.ProgressDialogFragment;

/**
 * Created by Object173
 * on 25.03.2017.
 */

public abstract class MyBaseActivity extends AppCompatActivity {

    private View contentView = null;
    private Toolbar toolbar = null;

    protected final boolean onCreate(final Bundle savedInstanceState, final int contentId,
                                     final String title) {
        try {
            super.onCreate(savedInstanceState);
            contentView = getLayoutInflater().inflate(contentId, null);
            setContentView(contentView);
        } catch (Exception ex) {
            finish();
            return false;
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            if (title != null) {
                toolbar.setTitle(title);
            }
            setSupportActionBar(toolbar);
        }
        return true;
    }

    protected final boolean onCreate(final Bundle savedInstanceState, final int contentId, final String title,
                                     final boolean buttonHomeEnabled) {
        final boolean isCreateBaseActivity = onCreate(savedInstanceState, contentId, title);

        if (isCreateBaseActivity) {
            setButtonHomeEnabled(buttonHomeEnabled);
        }
        return isCreateBaseActivity;
    }

    protected final boolean onCreate(final Bundle savedInstanceState, final int contentId,
                                     final boolean buttonHomeEnabled) {
        boolean isCreateBaseActivity = onCreate(savedInstanceState, contentId,
                getString(R.string.app_name));

        if (isCreateBaseActivity) {
            setButtonHomeEnabled(buttonHomeEnabled);
        }
        return isCreateBaseActivity;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setButtonHomeEnabled(final boolean enabled) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
            getSupportActionBar().setDisplayShowHomeEnabled(enabled);
        }
    }

    public final Toolbar getToolbar() {
        return toolbar;
    }

    public final void setToolbarTitle(final String title) {
        if (title == null) {
            return;
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    public final void showProgressDialog() {
        if (getSupportFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG) == null) {
            final ProgressDialogFragment mProgressDialog = new ProgressDialogFragment();
            mProgressDialog.show(getSupportFragmentManager());
        }
        else {
            final ProgressDialogFragment mProgressDialog =
                    (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG);
            if(!mProgressDialog.isShowing()) {
                mProgressDialog.show(getSupportFragmentManager());
            }
        }
    }

    public final void hideProgressDialog() {
        final ProgressDialogFragment mProgressDialog =
                (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG);
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public final void showSnackbar(final int message, final boolean isLong) {
        if(contentView == null) {
            return;
        }
        Snackbar.make(contentView, message, isLong?Snackbar.LENGTH_LONG:Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item == null) {
            return false;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
