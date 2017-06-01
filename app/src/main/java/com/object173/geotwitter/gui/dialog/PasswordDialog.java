package com.object173.geotwitter.gui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.base.MyDialogFragment;
import com.object173.geotwitter.util.user.UserContract;

public final class PasswordDialog extends MyDialogFragment {

    private EditText oldPasswordField;
    private EditText newPasswordField;
    private EditText conformPasswordField;

    private final DialogInterface.OnShowListener showListener = new DialogInterface.OnShowListener() {
        @Override
        public void onShow(DialogInterface dialog) {
            final Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if(!getShowsDialog()) {
                        return;
                    }
                    final String oldPassword = oldPasswordField.getText().toString();
                    final String newPassword = newPasswordField.getText().toString();
                    final String conformPassword = conformPasswordField.getText().toString();

                    if(newPassword.length() < UserContract.PASSWORD_MIN_LENGTH) {
                        newPasswordField.setError(getString(R.string.edit_dialog_error_min_length)
                                + Integer.toString(UserContract.PASSWORD_MIN_LENGTH));
                        return;
                    }
                    if(newPassword.length() > UserContract.PASSWORD_MAX_LENGTH) {
                        newPasswordField.setError(getString(R.string.edit_dialog_error_max_length)
                                + Integer.toString(UserContract.PASSWORD_MAX_LENGTH));
                        return;
                    }
                    if(!newPassword.equals(conformPassword)) {
                        conformPasswordField.setError(getString(R.string.password_dialog_error_conform_password));
                        return;
                    }
                    if(mListener != null) {
                        mListener.result(oldPassword, newPassword);
                    }
                    dismiss();
                }
            });
        }
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_edit_password, null);

        oldPasswordField = (EditText) view.findViewById(R.id.old_password_field);
        newPasswordField = (EditText) view.findViewById(R.id.new_password_field);
        conformPasswordField = (EditText) view.findViewById(R.id.conform_password_field);

        builder.setTitle(getString(R.string.password_dialog_title));
        builder.setView(view);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, null);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(showListener);

        return dialog;
    }

    private PasswordDialogResult mListener;

    public interface PasswordDialogResult {
        void result(String oldPassword, String newPassword);
    }

    @Override
    public void onAttach(final Context context) {
        mListener = (PasswordDialogResult) context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }
}
