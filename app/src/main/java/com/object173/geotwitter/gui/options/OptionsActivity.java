package com.object173.geotwitter.gui.options;

import android.os.Bundle;
import android.view.MenuItem;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.base.MyBaseActivity;

public class OptionsActivity extends MyBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_options, null, true);
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        return item != null && super.onOptionsItemSelected(item);
    }
}
