package com.object173.geotwitter.gui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.base.MyDialogFragment;

public final class EditTextDialog extends MyDialogFragment {

    private static final String KEY_TITLE = "title";
    private static final String KEY_VALUE = "value";
    private static final String KEY_HINT = "hint";
    private static final String KEY_MIN_LENGTH = "min_length";
    private static final String KEY_MAX_LENGTH = "max_length";
    private static final String KEY_TAG = "tag";

    private EditText editText;
    private int minLength;
    private int maxLength;

    public static EditTextDialog newInstance(final String title, final String hint, final String value,
                                             final int minLength, final int maxLength, final int tag) {
        if(title == null || minLength < 0 || maxLength < minLength) {
            return null;
        }
        final EditTextDialog dialog = new EditTextDialog();
        final Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_VALUE, value);
        bundle.putString(KEY_HINT, hint);
        bundle.putInt(KEY_MIN_LENGTH, minLength);
        bundle.putInt(KEY_MAX_LENGTH, maxLength);
        bundle.putInt(KEY_TAG, tag);
        dialog.setArguments(bundle);
        return dialog;
    }

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
                    final String text = editText.getText().toString();

                    if(text.length() < minLength) {
                        editText.setError(getString(R.string.edit_dialog_error_min_length) + Integer.toString(minLength));
                        return;
                    }
                    if(text.length() > maxLength) {
                        editText.setError(getString(R.string.edit_dialog_error_max_length) + Integer.toString(maxLength));
                        return;
                    }
                    if(mListener != null) {
                        mListener.result(getArguments().getInt(KEY_TAG), text);
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

        final Bundle args = getArguments();

        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_edit_text, null);

        editText = (EditText) view.findViewById(R.id.edit_text_field);
        editText.setText(args.getString(KEY_VALUE));

        final TextInputLayout inputLayout = (TextInputLayout) view.findViewById(R.id.text_input);
        inputLayout.setHint(args.getString(KEY_HINT));

        minLength = args.getInt(KEY_MIN_LENGTH);
        maxLength = args.getInt(KEY_MAX_LENGTH);

        builder.setTitle(args.getString(KEY_TITLE));
        builder.setView(view);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, null);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(showListener);

        return dialog;
    }

    private EditTextDialogResult mListener;

    public interface EditTextDialogResult {
        void result(int tag, String text);
    }

    @Override
    public void onAttach(final Context context) {
        mListener = (EditTextDialogResult) context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }
}
