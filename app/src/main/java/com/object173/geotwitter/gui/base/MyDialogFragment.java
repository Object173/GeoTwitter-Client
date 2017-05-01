package com.object173.geotwitter.gui.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by Object173
 * on 30.03.2017.
 */

public abstract class MyDialogFragment extends DialogFragment {

    protected String fragmentTag = "MyDialogFragment";

    @NonNull
    @Override
    public abstract Dialog onCreateDialog(@Nullable Bundle savedInstanceState);

    public final void show(final FragmentManager manager) {
        if(manager != null) {
            final FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, fragmentTag);
            ft.commit();
        }
    }

    public final boolean isShowing() {
        return getShowsDialog();
    }
}
