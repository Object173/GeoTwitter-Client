package com.object173.geotwitter.gui.profile;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.object173.geotwitter.R;
import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.entities.Image;
import com.object173.geotwitter.database.entities.Profile;
import com.object173.geotwitter.database.service.ProfileService;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.gui.images.ImageViewerActivity;
import com.object173.geotwitter.gui.messenger.DialogActivity;
import com.object173.geotwitter.gui.place.PlaceListActivity;
import com.object173.geotwitter.gui.util.ServiceConnectionController;
import com.object173.geotwitter.server.json.AuthProfile;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.service.BaseServiceTask;
import com.object173.geotwitter.service.contacts.ContactsService;
import com.object173.geotwitter.service.messenger.LoadDialogsTask;
import com.object173.geotwitter.service.messenger.MessengerService;
import com.object173.geotwitter.util.resources.ImagesManager;

public class ProfileActivity extends MyBaseActivity
        implements ServiceConnectionController.ServiceConnector,
                    NavigationView.OnNavigationItemSelectedListener {

    private long userId = DatabaseContract.NULL_ID;

    private boolean isContact = false;
    private long profileId = DatabaseContract.NULL_ID;

    private static final String KEY_ONLINE_PROFILE = "online_profile";
    private static final String KEY_LOCAL_PROFILE = "local_profile";

    private final ServiceConnectionController connectionController = new ServiceConnectionController();

    public static void startActivity(final Context context, final AuthProfile profile) {
        if(context == null || profile == null) {
            return;
        }
        final Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(KEY_ONLINE_PROFILE, profile);
        context.startActivity(intent);
    }

    public static Intent getStartIntent(final Context context, final long id) {
        if(context == null) {
            return null;
        }
        final Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(KEY_LOCAL_PROFILE, id);
        return intent;
    }

    public static void startActivity(final Context context, final long id) {
        final Intent intent = getStartIntent(context, id);
        if(intent != null) {
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!super.onCreate(savedInstanceState, R.layout.activity_profile, true)) {
            finish();
            return;
        }

        profileId = getIntent().getLongExtra(KEY_LOCAL_PROFILE, DatabaseContract.NULL_ID);
        if(profileId > DatabaseContract.NULL_ID) {
            initProfile(profileId);
        }
        else {
            isContact = false;
            final AuthProfile authProfile = (AuthProfile) getIntent().getExtras().getSerializable(KEY_ONLINE_PROFILE);
            initProfile(authProfile);
        }

        connectionController.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectionController.onResume(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        connectionController.onPause(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        connectionController.onSaveInstanceState(outState);
    }

    private void initProfile(final AuthProfile profile) {
        if(profile == null) {
            return;
        }
        userId = profile.getUserId();

        final ImageView avatarView = (ImageView) findViewById(R.id.avatar_view);
        if(profile.getAvatarUrl() != null && profile.getAvatarUrl().length() > 0) {
            ImagesManager.setImageViewOnline(this, avatarView, R.mipmap.avatar, profile.getAvatarUrl());

            avatarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageViewerActivity.startActivity(ProfileActivity.this, Image.newNetworkImage(profile.getAvatarUrl()),
                            profile.getUsername());
                }
            });
        }

        initNavigationView(profile.getUsername(), profile.getStatus());
        initFab(null);
    }

    private void initProfile(final long profileId) {
        final Cursor cursor = ProfileService.getContact(this, profileId);
        if(cursor == null || cursor.getCount() <= 0) {
            finish();
            return;
        }

        initProfile(cursor);

        cursor.registerContentObserver(new ContentObserver(new Handler()) {
            @Override
            public boolean deliverSelfNotifications() {
                return true;
            }

            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                cursor.requery();
                initProfile(cursor);
            }
        });
    }

    private void initProfile(final Cursor cursor) {
        if(cursor == null || !cursor.moveToFirst()) {
            return;
        }

        final Profile profile = ProfileService.getContact(this, cursor);
        userId = profile.getUserId();

        isContact = profile.getRelationStatus().equals(AuthProfile.RelationStatus.CONTACT);

        final ImageView avatarView = (ImageView) findViewById(R.id.avatar_view);
        if(profile.getAvatar() != null) {
            ImagesManager.setImageViewCache(this, avatarView, R.mipmap.avatar, profile.getAvatar());

            avatarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageViewerActivity.startActivity(ProfileActivity.this, profile.getAvatar(), profile.getUsername());
                }
            });
        }

        initNavigationView(profile.getUsername(), profile.getStatus());
        initFab(profile.getRelationStatus());
    }

    private void initNavigationView(final String username, final String status) {
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        final MenuItem usernameField = navigationView.getMenu().findItem(R.id.username_field);
        final TextView statusField = (TextView) navigationView.getMenu().findItem(R.id.status_field)
                .getActionView().findViewById(R.id.text_view);

        usernameField.setTitle(username);
        statusField.setText(status);

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initFab(final AuthProfile.RelationStatus relation) {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_button);
        if(relation == null) {
            fab.setVisibility(View.GONE);
            return;
        }
        else {
            fab.setVisibility(View.VISIBLE);
        }
        if(relation.equals(AuthProfile.RelationStatus.CONTACT)) {
            fab.setImageResource(R.mipmap.ic_message_white);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    connectionController.setRequestId(
                            MessengerService.startToGetDialog(ProfileActivity.this, userId));
                }
            });
        } else
        if(relation.equals(AuthProfile.RelationStatus.INVITE)) {
            fab.setImageResource(R.mipmap.ic_account_minus_white);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    connectionController.setRequestId(
                            ContactsService.startToRemoveInvite(ProfileActivity.this, userId));
                }
            });
        }
        else {
            fab.setImageResource(R.mipmap.ic_add_contact_white);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    connectionController.setRequestId(
                            ContactsService.startToSendInvite(ProfileActivity.this, userId));
                }
            });
        }
    }

    @Override
    public void receiveMessage(Intent intent) {
    }

    @Override
    public void finishTask(Intent intent) {
        if(intent == null) {
            return;
        }
        final AuthResult.Result result = BaseServiceTask.getAuthResult(intent);

        if(result.equals(AuthResult.Result.NO_INTERNET)) {
            showSnackbar(R.string.login_activity_message_no_internet, false);
        } else
        if(result.equals(AuthResult.Result.FAIL)) {
            showSnackbar(R.string.login_activity_message_error_server, false);
        }
        if(LoadDialogsTask.isGetIntent(intent)) {
            final long dialogId = LoadDialogsTask.getDialogId(intent);
            DialogActivity.startActivity(ProfileActivity.this, dialogId);
        }
    }

    @Override
    public void finishTask(Class serviceClass) {
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.place_list_field:
                if(isContact) {
                    PlaceListActivity.startActivity(ProfileActivity.this, profileId);
                }
                else {
                    showSnackbar(R.string.profile_activity_message_place_access_denied, false);
                }
                return true;
            default:
                return false;
        }
    }
}
