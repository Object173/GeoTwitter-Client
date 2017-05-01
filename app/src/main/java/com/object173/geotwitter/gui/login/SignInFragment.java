package com.object173.geotwitter.gui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.ServiceConnectionController;
import com.object173.geotwitter.gui.base.MyBaseFragment;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.services.authorization.AuthService;
import com.object173.geotwitter.services.authorization.SignInTask;

/**
 * Created by Object173
 * on 27.04.2017.
 */

public final class SignInFragment extends MyBaseFragment
        implements View.OnClickListener, ServiceConnectionController.ServiceConnector{

    private final ServiceConnectionController serviceController =
            new ServiceConnectionController(AuthService.class, AuthService.ACTION);

    private EditText mLoginField;
    private EditText mPasswordField;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sing_in, container, false);

        mLoginField = (EditText) view.findViewById(R.id.login_field);
        mPasswordField = (EditText) view.findViewById(R.id.password_field);

        view.findViewById(R.id.sign_in_button).setOnClickListener(this);

        serviceController.onCreate(savedInstanceState);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceController.onResume(getContext(), this);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        serviceController.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        serviceController.onPause(getContext());
    }

    private void signIn(final String email, final String password) {
        if (getContext() == null || !validateForm()) {
            return;
        }
        showProgressDialog();
        serviceController.setRequestId(AuthService.startToSignIn(getContext(), email, password));
    }

    private boolean validateForm() {
        boolean valid = true;

        final String email = mLoginField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mLoginField.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            mLoginField.setError(null);
        }

        final String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(final View view) {
        if(view == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn(mLoginField.getText().toString(), mPasswordField.getText().toString());
                break;
        }
    }

    @Override
    public void receiveMessage(final Intent intent) {

    }

    @Override
    public void finishTask(final Intent intent) {
        hideProgressDialog();

        if(intent != null && SignInTask.isThisIntent(intent)) {
            final AuthResult.Result result = SignInTask.getAuthResult(intent);

            if(result.equals(AuthResult.Result.SUCCESS)) {
                final LoginActivity activity = (LoginActivity) getActivity();
                if(activity != null) {
                    activity.finishSignIn();
                }
            } else
            if(result.equals(AuthResult.Result.NO_INTERNET)) {
                showSnackbar(R.string.login_activity_message_no_internet, false);
            } else
            if(result.equals(AuthResult.Result.USER_NOT_FOUND)) {
                showSnackbar(R.string.login_activity_message_user_not_found, false);
            } else
            if(result.equals(AuthResult.Result.INCORRECT_PASSWORD)) {
                showSnackbar(R.string.login_activity_message_incorrect_password, false);
            } else
            if(result.equals(AuthResult.Result.WRONG_PASSWORD)) {
                showSnackbar(R.string.login_activity_message_wrong_password, false);
            } else
            if(result.equals(AuthResult.Result.FAIL)) {
                showSnackbar(R.string.login_activity_message_error_server, false);
            }
        }
    }

    @Override
    public void finishTask(Class serviceClass) {
        hideProgressDialog();
    }
}
