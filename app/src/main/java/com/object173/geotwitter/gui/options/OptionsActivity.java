package com.object173.geotwitter.gui.options;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.base.MyBaseActivity;

public final class OptionsActivity extends MyBaseActivity {

    private static final String KEY_FRAGMENT = "fragment";

    private static final String FRAGMENT_MAP = "map";
    private static final String FRAGMENT_NOTIFICATIONS = "notifications";

    private static final String FRAGMENT_TAG = "fragment_tag";

    public static void startMapActivity(final Context context) {
        startActivity(context, FRAGMENT_MAP);
    }

    public static void startNotificationsActivity(final Context context) {
        startActivity(context, FRAGMENT_NOTIFICATIONS);
    }

    private static void startActivity(final Context context, final String fragment) {
        if(context == null || fragment == null) {
            return;
        }
        final Intent intent = new Intent(context, OptionsActivity.class);
        intent.putExtra(KEY_FRAGMENT, fragment);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!super.onCreate(savedInstanceState, R.layout.activity_options, true)) {
            finish();
            return;
        }

        Fragment fragment = getFragmentManager() .findFragmentByTag(FRAGMENT_TAG);
        if(fragment == null) {
            final String current_fragment = getIntent().getExtras().getString(KEY_FRAGMENT);
            switch (current_fragment) {
                case FRAGMENT_MAP:
                    setToolbarTitle(getString(R.string.preference_title_map_category));
                    fragment = OptionsFragment.newInstance(this, R.xml.preference_map);
                    break;
                case FRAGMENT_NOTIFICATIONS:
                    setToolbarTitle(getString(R.string.preference_title_notifications_category));
                    fragment = OptionsFragment.newInstance(this, R.xml.preference_notifications);
            }
            if(fragment != null) {
                getFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, FRAGMENT_TAG)
                        .commit();
            }
            else {
                finish();
            }
        }
    }
}
