package com.object173.geotwitter.gui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.ServiceConnectionController;
import com.object173.geotwitter.gui.base.MyBaseFragment;
import com.object173.geotwitter.gui.views.CircleImageView;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.services.authorization.AuthService;
import com.object173.geotwitter.services.authorization.RegisterTask;
import com.object173.geotwitter.util.AvatarManager;
import com.object173.geotwitter.util.ChooseImageManager;

/**
 * Created by Object173
 * on 27.04.2017.
 */

public final class RegisterFragment extends MyBaseFragment
        implements View.OnClickListener, ServiceConnectionController.ServiceConnector{

    public static final int INTENT_PICK_IMAGE = 173;

    private final ServiceConnectionController serviceController =
            new ServiceConnectionController(AuthService.class, AuthService.ACTION);

    private EditText mUsernameField;
    private EditText mLoginField;
    private EditText mPasswordField;

    private CircleImageView avatarImage;
    private boolean isAvatar = false;
    private boolean changeAvatar = false;

    private static final String KEY_IS_AVATAR = "is_avatar";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("RegisterFragment", "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_register, container, false);
        Log.d("RegisterFragment", "onCreateView");

        mUsernameField = (EditText) view.findViewById(R.id.username_field);
        mLoginField = (EditText) view.findViewById(R.id.login_field);
        mPasswordField = (EditText) view.findViewById(R.id.password_field);

        view.findViewById(R.id.register_button).setOnClickListener(this);

        avatarImage = (CircleImageView) view.findViewById(R.id.imageViewAvatar);

        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImageManager.showImageChooser(getActivity(),
                        getString(R.string.register_fragment_title_image_picker), INTENT_PICK_IMAGE);
            }
        });


        serviceController.onCreate(savedInstanceState);
        if(savedInstanceState != null && !changeAvatar) {
            isAvatar = savedInstanceState.getBoolean(KEY_IS_AVATAR, false);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceController.onResume(getContext(), this);
        setAvatar(isAvatar);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        serviceController.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_AVATAR, isAvatar);
    }

    @Override
    public void onPause() {
        super.onPause();
        serviceController.onPause(getContext());
    }

    @Override
    public void onClick(final View view) {
        if(view == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.register_button:
                createAccount(mUsernameField.getText().toString(), mLoginField.getText().toString(),
                        mPasswordField.getText().toString());
                break;
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        final String login = mLoginField.getText().toString();
        if (TextUtils.isEmpty(login)) {
            mLoginField.setError(getString(R.string.error_field_required));
            valid = false;
        }
        else {
            mLoginField.setError(null);
        }

        final String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError(getString(R.string.error_field_required));
            valid = false;
        }
        else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void createAccount(final String username, final String login, final String password) {
        if (getContext() == null || !validateForm()) {
            return;
        }
        showProgressDialog();

        serviceController.setRequestId(AuthService.startToRegister(getContext(), username,
                login, password, AvatarManager.getAvatarPath(getContext())));
    }

    @Override
    public void receiveMessage(Intent intent) {

    }

    @Override
    public void finishTask(final Intent intent) {
        hideProgressDialog();

        if(intent != null && RegisterTask.isThisIntent(intent)) {
            final AuthResult.Result result = RegisterTask.getAuthResult(intent);

            if(result.equals(AuthResult.Result.SUCCESS)) {
                final LoginActivity activity = (LoginActivity) getActivity();
                if(activity != null) {
                    activity.finishSignIn();
                }
            } else
            if(result.equals(AuthResult.Result.NO_INTERNET)) {
                showSnackbar(R.string.login_activity_message_no_internet, false);
            } else
            if(result.equals(AuthResult.Result.INCORRECT_LOGIN)) {
                showSnackbar(R.string.login_activity_message_incorrect_login, false);
            } else
            if(result.equals(AuthResult.Result.INCORRECT_PASSWORD)) {
                showSnackbar(R.string.login_activity_message_incorrect_password, false);
            } else
            if(result.equals(AuthResult.Result.LOGIN_EXISTS)) {
                showSnackbar(R.string.login_activity_message_login_exists, false);
            } else
            if(result.equals(AuthResult.Result.FAIL)) {
                showSnackbar(R.string.login_activity_message_error_server, false);
            }
        }
    }

    public final void setAvatar(final boolean isAvatar) {
        this.isAvatar = isAvatar;
        this.changeAvatar = true;

        if(avatarImage != null) {
            this.changeAvatar = false;
            if(this.isAvatar) {
                avatarImage.setImageResource(0);
                avatarImage.setImageURI(AvatarManager.getAvatarUri(getContext()));
            }
            else {
                avatarImage.setImageResource(R.mipmap.avatar);
            }
        }
    }

    @Override
    public void finishTask(Class serviceClass) {
        hideProgressDialog();
    }
}
