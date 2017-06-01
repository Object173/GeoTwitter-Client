package com.object173.geotwitter.gui.options;

import android.os.Bundle;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.base.MyBaseActivity;

public final class OptionsActivity extends MyBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!super.onCreate(savedInstanceState, R.layout.activity_options, true)) {
            finish();
        }
    }
}
