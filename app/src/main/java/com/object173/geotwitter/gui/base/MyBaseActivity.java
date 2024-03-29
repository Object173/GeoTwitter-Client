package com.object173.geotwitter.gui.base;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.dialog.ProgressDialogFragment;
import com.object173.geotwitter.server.json.AuthResult;

/**
 * Created by Object173
 * on 25.03.2017.
 */

public abstract class MyBaseActivity extends AppCompatActivity {

    private View contentView = null;
    private Toolbar toolbar = null;

    protected final boolean onCreate(final Bundle savedInstanceState, final int contentId,
                                     final String title) {
        super.onCreate(savedInstanceState);
        try {
            contentView = getLayoutInflater().inflate(contentId, null);
            setContentView(contentView);
        } catch (Exception ex) {
            ex.printStackTrace();
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
        boolean isCreateBaseActivity = onCreate(savedInstanceState, contentId, null);

        if (isCreateBaseActivity) {
            setButtonHomeEnabled(buttonHomeEnabled);
        }
        return isCreateBaseActivity;
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

    public final void setToolbarPadding() {
        final ActionBar actionBar = getSupportActionBar();
        if (toolbar != null) {
            final int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                final int padding = getResources().getDimensionPixelSize(resourceId);
                toolbar.setPadding(0, padding, 0, 0);
            }
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
        Log.d("BaseActivity","showSnackbar");
        if(contentView == null) {
            return;
        }
        Snackbar.make(contentView, message, isLong ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    protected void showReceiveResult(final AuthResult.Result result) {
        if(result.equals(AuthResult.Result.NO_INTERNET)) {
            showSnackbar(R.string.login_activity_message_no_internet, false);
        } else
        if(result.equals(AuthResult.Result.FAIL)) {
            showSnackbar(R.string.login_activity_message_error_server, false);
        }
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
