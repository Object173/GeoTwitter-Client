package com.object173.geotwitter.gui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import com.object173.geotwitter.R;
import com.object173.geotwitter.server.json.AuthResult;

public abstract class MyBaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public final void showProgressDialog() {
        final MyBaseActivity activity = (MyBaseActivity) getActivity();

        if(activity != null) {
            activity.showProgressDialog();
        }
    }

    public final void hideProgressDialog() {
        final MyBaseActivity activity = (MyBaseActivity) getActivity();
        if(activity != null) {
            activity.hideProgressDialog();
        }
    }

    public final void showSnackbar(final int message, final boolean isLong) {
        if(getView() != null) {
            Snackbar.make(getView(), message, isLong ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
    }

    protected void showReceiveResult(final AuthResult.Result result) {
        if(result.equals(AuthResult.Result.NO_INTERNET)) {
            showSnackbar(R.string.login_activity_message_no_internet, false);
        } else
        if(result.equals(AuthResult.Result.FAIL)) {
            showSnackbar(R.string.login_activity_message_error_server, false);
        }
    }
}
