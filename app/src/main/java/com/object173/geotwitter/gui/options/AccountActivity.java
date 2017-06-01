package com.object173.geotwitter.gui.options;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.gui.main.news.PlaceListFragment;
import com.object173.geotwitter.gui.profile.ProfilePreferenceActivity;
import com.object173.geotwitter.gui.views.CircleImageView;
import com.object173.geotwitter.util.user.AuthManager;
import com.object173.geotwitter.util.user.AvatarManager;

public class AccountActivity extends MyBaseActivity{

    private CircleImageView avatarView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if(!super.onCreate(savedInstanceState, R.layout.activity_account, true) || !AuthManager.isAuth()) {
            finish();
            return;
        }
        initMenu();

        final FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(PlaceListFragment.TAG);
        if(fragment == null) {
            fragment = PlaceListFragment.newInstance(AuthManager.getAuthToken().getUserId());
            fm.beginTransaction().add(R.id.frame_layout, fragment, PlaceListFragment.TAG).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AvatarManager.setAvatarView(this, avatarView);
    }

    private void initMenu() {
        final LinearLayout profileView = (LinearLayout) findViewById(R.id.profile_layout);

        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view != null) {
                    startActivity(new Intent(AccountActivity.this, ProfilePreferenceActivity.class));
                }
            }
        });

        final TextView usernameView = (TextView) profileView.findViewById(R.id.username_text_view);
        usernameView.setText(AuthManager.getAuthProfile().getUsername());

        final TextView statusView = (TextView) profileView.findViewById(R.id.status_text_view);
        statusView.setText(AuthManager.getAuthProfile().getStatus());

        avatarView = (CircleImageView) profileView.findViewById(R.id.image_view_avatar);
        AvatarManager.setAvatarView(this, avatarView);
    }
}
