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
import com.object173.geotwitter.service.authorization.AuthService;
import com.object173.geotwitter.service.authorization.RegisterTask;
import com.object173.geotwitter.util.resources.CacheManager;
import com.object173.geotwitter.util.resources.ChooseImageManager;
import com.object173.geotwitter.util.resources.ImagesManager;

/**
 * Created by Object173
 * on 27.04.2017.
 */

public final class RegisterFragment extends MyBaseFragment
        implements View.OnClickListener, ServiceConnectionController.ServiceConnector,
                    LoginActivity.OnChangeAvatarListener{

    private final ServiceConnectionController serviceController =
            new ServiceConnectionController();

    private EditText mUsernameField;
    private EditText mLoginField;
    private EditText mPasswordField;

    private CircleImageView avatarImage;
    private boolean isAvatar = false;
    private boolean changeAvatar = false;

    private static final String KEY_IS_AVATAR = "is_avatar";

    public static final String CACHE_AVATAR = "cash_avatar.png";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("RegisterFragment", "isAvatar = "+isAvatar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_register, container, false);

        mUsernameField = (EditText) view.findViewById(R.id.username_field);
        mLoginField = (EditText) view.findViewById(R.id.login_field);
        mPasswordField = (EditText) view.findViewById(R.id.password_field);

        view.findViewById(R.id.register_button).setOnClickListener(this);

        avatarImage = (CircleImageView) view.findViewById(R.id.image_view_avatar);

        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImageManager.showImageChooser(getActivity(),
                        getString(R.string.register_fragment_title_image_picker),
                        CacheManager.getImagePath(CACHE_AVATAR));
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

        final LoginActivity activity = (LoginActivity)getActivity();
        if(activity != null) {
            activity.setListener(this);
        }

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

        final boolean startTask;
        if(isAvatar) {
            startTask = serviceController.setRequestId(AuthService.startToRegister(getContext(), username,
                    login, password, CacheManager.getImagePath(CACHE_AVATAR)));
        }
        else {
            startTask = serviceController.setRequestId(AuthService.startToRegister(getContext(), username,
                    login, password, null));
        }
        if(startTask) {
            showProgressDialog();
        }
    }

    @Override
    public void receiveMessage(Intent intent) {
        if(intent != null && RegisterTask.isThisIntent(intent)) {
            hideProgressDialog();

            final AuthResult.Result result = RegisterTask.getAuthResult(intent);

            if(result.equals(AuthResult.Result.SUCCESS)) {
                final LoginActivity activity = (LoginActivity) getActivity();
                if(activity != null) {
                    ImagesManager.deleteImage(CacheManager.getImagePath(RegisterFragment.CACHE_AVATAR));
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

    @Override
    public void finishTask(final Intent intent) {
        hideProgressDialog();
        receiveMessage(intent);
    }

    @Override
    public final void setAvatar(final boolean isAvatar) {
        this.isAvatar = isAvatar;
        this.changeAvatar = true;

        if(avatarImage != null) {
            this.changeAvatar = false;
            if(this.isAvatar) {
                ImagesManager.setImageViewCache(getContext(), avatarImage, R.mipmap.avatar,
                        CacheManager.getImagePath(RegisterFragment.CACHE_AVATAR));
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
