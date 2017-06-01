package com.object173.geotwitter.gui.messenger;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.Image;
import com.object173.geotwitter.database.entities.Profile;
import com.object173.geotwitter.database.service.ProfileService;
import com.object173.geotwitter.gui.profile.ProfileActivity;
import com.object173.geotwitter.util.resources.ImagesManager;

public final class ProfileToolbar {

    private final long profileId;

    private Context context;
    private Toolbar toolbar;

    private String username;
    private Image avatar;

    private static final String KEY_USERNAME = "username";
    private static final String KEY_AVATAR_LOCAL = "avatar_local";
    private static final String KEY_AVATAR_ONLINE = "avatar_online";

    public ProfileToolbar(final long profileId) {
        this.profileId = profileId;
    }

    public void onCreate(final Context context, final Bundle saveInstanceState, final Toolbar toolbar) {
        if(context == null || toolbar == null) {
            return;
        }
        this.context = context;
        this.toolbar = toolbar;

        if(saveInstanceState != null) {
            username = saveInstanceState.getString(KEY_USERNAME);
            final String localPath = saveInstanceState.getString(KEY_AVATAR_LOCAL);
            final String onlineUrl = saveInstanceState.getString(KEY_AVATAR_ONLINE);
            if(localPath != null || onlineUrl != null) {
                avatar = new Image(localPath, onlineUrl);
            }
        }
        if(username != null) {
            setProfile(username, avatar);
        }
        else {
            final LoadProfileTask task = new LoadProfileTask();
            task.execute(profileId);
        }
    }

    private void setProfile(final String username, final Image avatar) {
        if(username == null) {
            return;
        }
        this.username = username;
        this.avatar = avatar;

        if(toolbar != null) {
            final TextView usernameField = (TextView) toolbar.findViewById(R.id.toolbar_title);
            if(usernameField != null) {
                usernameField.setText(this.username);
            }
            final ImageView avatarView = (ImageView) toolbar.findViewById(R.id.toolbar_icon);
            ImagesManager.setImageViewCache(context, avatarView, R.mipmap.avatar, avatar);

            final LinearLayout layout = (LinearLayout) toolbar.findViewById(R.id.toolbar_action);
            if(layout != null) {
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ProfileActivity.startActivity(context, profileId);
                    }
                });
            }
        }
    }

    public void onSaveInstanceState(final Bundle outState) {
        if(outState != null) {
            outState.putString(KEY_USERNAME, username);
            if(avatar != null) {
                outState.putString(KEY_AVATAR_LOCAL, avatar.getLocalPath());
                outState.putString(KEY_AVATAR_ONLINE, avatar.getOnlineUrl());
            }
        }
    }

    private class LoadProfileTask extends AsyncTask<Long, Void, Profile> {

        @Override
        protected Profile doInBackground(Long... args) {
            if(args != null && args.length > 0) {
                return ProfileService.getProfile(context, args[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Profile profile) {
            super.onPostExecute(profile);
            if(profile != null) {
                setProfile(profile.getUsername(), profile.getAvatarMini());
            }
        }
    }
}
