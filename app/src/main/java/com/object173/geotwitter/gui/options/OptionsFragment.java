package com.object173.geotwitter.gui.options;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

/**
 * Created by Object173
 * on 25.04.2017.
 */

public final class OptionsFragment extends PreferenceFragment {

    private static final String KEY_LAYOUT = "content_layout";

    public static OptionsFragment newInstance(final Context context, final int layout) {
        if(context == null || layout <= 0) {
            return null;
        }
        final OptionsFragment fragment = new OptionsFragment();
        final Bundle bundle = new Bundle();
        bundle.putInt(KEY_LAYOUT, layout);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int contentLayout = getArguments().getInt(KEY_LAYOUT);
        addPreferencesFromResource(contentLayout);
    }
}
