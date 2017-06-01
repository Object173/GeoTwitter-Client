package com.object173.geotwitter.gui.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.base.MyDialogFragment;

public final class ProgressDialogFragment extends MyDialogFragment {

    public static final String TAG = "ProgressDialogFragment";

   public ProgressDialogFragment() {
       fragmentTag = TAG;
   }

    @NonNull
    @Override
    public final Dialog onCreateDialog(final Bundle savedInstanceState) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.progress_dialog_title_loading));
        setCancelable(false);

        return progressDialog;
    }

}
