package com.object173.geotwitter.gui.options;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.object173.geotwitter.R;

/**
 * Created by Object173
 * on 25.04.2017.
 */

public final class OptionsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);
    }

    @Override
    public final void onResume() {
        super.onResume();

        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        if(preferenceScreen != null) {
            preferenceScreen.getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public final void onPause() {
        super.onPause();

        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        if(preferenceScreen != null) {
            preferenceScreen.getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(null);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}
