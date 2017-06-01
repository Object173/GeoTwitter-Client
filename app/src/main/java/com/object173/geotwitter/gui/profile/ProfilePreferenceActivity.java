package com.object173.geotwitter.gui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.gui.dialog.EditTextDialog;
import com.object173.geotwitter.gui.dialog.PasswordDialog;
import com.object173.geotwitter.gui.util.DeleteImageActivity;
import com.object173.geotwitter.gui.util.ServiceConnectionController;
import com.object173.geotwitter.gui.views.CircleImageView;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.service.authorization.AuthService;
import com.object173.geotwitter.service.authorization.EditProfileTask;
import com.object173.geotwitter.util.resources.CacheManager;
import com.object173.geotwitter.util.resources.ChooserManager;
import com.object173.geotwitter.util.user.AuthManager;
import com.object173.geotwitter.util.user.AvatarManager;
import com.object173.geotwitter.util.user.UserContract;
import com.soundcloud.android.crop.Crop;

public class ProfilePreferenceActivity extends MyBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    ServiceConnectionController.ServiceConnector,
        EditTextDialog.EditTextDialogResult,
        PasswordDialog.PasswordDialogResult {

    private final ServiceConnectionController connectionController =
            new ServiceConnectionController();

    private static final int TAG_USERNAME = 11;
    private static final int TAG_STATUS = 22;

    private static final String CACHE_AVATAR = "cash_avatar.png";

    private CircleImageView avatarView;

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        if(!super.onCreate(savedInstanceState, R.layout.activity_profile_preference, true)) {
            finish();
            return;
        }
        initProfile();
        connectionController.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectionController.onResume(this, this);

        AvatarManager.setAvatarView(this, avatarView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        connectionController.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        connectionController.onPause(this);
    }

    private void initProfile() {
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        final MenuItem usernameField = navigationView.getMenu().findItem(R.id.username_field);
        final TextView statusField = (TextView) navigationView.getMenu().findItem(R.id.status_field)
                .getActionView().findViewById(R.id.text_view);

        usernameField.setTitle(AuthManager.getAuthProfile().getUsername());
        statusField.setText(AuthManager.getAuthProfile().getStatus());

        avatarView = (CircleImageView) findViewById(R.id.image_view_avatar);
        AvatarManager.setAvatarView(this, avatarView);

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooserManager.showImageChooser(ProfilePreferenceActivity.this,
                        getString(R.string.register_fragment_title_image_picker), "");
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.username_field:
                EditTextDialog.newInstance(getString(R.string.profile_activity_title_edit_username),
                        getString(R.string.profile_activity_title_username), AuthManager.getAuthProfile().getUsername(),
                        UserContract.USERNAME_MIN_LENGTH, UserContract.USERNAME_MAX_LENGTH, TAG_USERNAME)
                        .show(getSupportFragmentManager());
                return true;
            case R.id.status_field:
                EditTextDialog.newInstance(getString(R.string.profile_activity_title_edit_status),
                        getString(R.string.profile_activity_title_status), AuthManager.getAuthProfile().getStatus(),
                        UserContract.STATUS_MIN_LENGTH, UserContract.STATUS_MAX_LENGTH, TAG_STATUS)
                        .show(getSupportFragmentManager());
                return true;
            case R.id.password_field:
                new PasswordDialog().show(getSupportFragmentManager());
                return true;
            case R.id.logout_field:
                AuthManager.signOut(this);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void result(final int tag, final String text) {
        if(text == null) {
            return;
        }
        boolean isTask = false;
        if(tag == TAG_USERNAME) {
            isTask = connectionController.setRequestId(AuthService.startToEditUsername(this, text));
        } else
        if(tag == TAG_STATUS) {
            isTask = connectionController.setRequestId(AuthService.startToEditStatus(this, text));
        }
        if(isTask) {
            showProgressDialog();
        }
    }

    @Override
    public void result(String oldPassword, String newPassword) {
        if(oldPassword == null || newPassword == null) {
            return;
        }

        if(connectionController.setRequestId(AuthService.startToEditPassword(this, oldPassword, newPassword))) {
            showProgressDialog();
        }
    }

    @Override
    public void receiveMessage(Intent intent) {
        if(EditProfileTask.isThisIntent(intent)) {
            final AuthResult.Result result = EditProfileTask.getAuthResult(intent);

            if (result.equals(AuthResult.Result.SUCCESS)) {
                initProfile();
            } else if (result.equals(AuthResult.Result.NO_INTERNET)) {
                showSnackbar(R.string.login_activity_message_no_internet, false);
            } else if (result.equals(AuthResult.Result.WRONG_PASSWORD)) {
                showSnackbar(R.string.login_activity_message_wrong_password, false);
            } else {
                showSnackbar(R.string.login_activity_message_error_server, false);
            }
        }
    }

    @Override
    public void finishTask(Intent intent) {
        hideProgressDialog();
        receiveMessage(intent);
    }

    @Override
    public void finishTask(Class serviceClass) {
        hideProgressDialog();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if(requestCode == ChooserManager.INTENT_PICK_IMAGE) {
            if(resultCode == RESULT_OK) {
                final Uri uri = ChooserManager.getImageUri(this, data);
                ChooserManager.selectImage(this, uri, CacheManager.getImagePath(CACHE_AVATAR));
            }
            else
            if(resultCode == DeleteImageActivity.RESULT_ACTIVITY) {
                connectionController.setRequestId(AuthService.startToRemoveAvatar(this));
            }
        } else
        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK && data!= null) {
            connectionController.setRequestId(AuthService.startToEditAvatar(this,
                    CacheManager.getImagePath(CACHE_AVATAR)));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
