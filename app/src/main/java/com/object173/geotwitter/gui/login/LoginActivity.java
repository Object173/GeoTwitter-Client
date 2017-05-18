package com.object173.geotwitter.gui.login;

import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.DeleteImageActivity;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.util.resources.CacheManager;
import com.object173.geotwitter.util.resources.ChooseImageManager;
import com.object173.geotwitter.util.resources.ImagesManager;
import com.object173.geotwitter.util.user.AuthManager;
import com.soundcloud.android.crop.Crop;

public class LoginActivity extends MyBaseActivity {

    private String accountType;

    public static final String ARG_ACCOUNT_TYPE = "ARG_ACCOUNT_TYPE";
    public static final String ARG_TOKEN_TYPE = "ARG_TOKEN_TYPE";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "ARG_IS_ADDING_NEW_ACCOUNT";

    private OnChangeAvatarListener listener;

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_login, null);

        accountType = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.login_activity_viewpager);
        final LoginPagerAdapter adapter = new LoginPagerAdapter(getSupportFragmentManager(),
                new String[]{getString(R.string.sign_in_fragment_title),
                        getString(R.string.register_fragment_title)});
        viewPager.setAdapter(adapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        finishSignIn();
    }

    public void finishSignIn() {
        if(AuthManager.isAuth()) {
            hideProgressDialog();
            createAccount();
            //finish();
            //startActivity(new Intent(this, MainActivity.class));
        }
    }

    public final void setListener(final OnChangeAvatarListener listener) {
        this.listener = listener;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if(requestCode == ChooseImageManager.INTENT_PICK_IMAGE) {
            if(resultCode == RESULT_OK) {
                final Uri uri = ChooseImageManager.getImageUri(this, data);
                ChooseImageManager.selectImage(this, uri, CacheManager.getImagePath(RegisterFragment.CACHE_AVATAR));
            }
            else
            if(resultCode == DeleteImageActivity.RESULT_ACTIVITY) {
                if(listener!= null) {
                    listener.setAvatar(false);
                }
            }
        } else
        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK && data!= null) {
            ImagesManager.invalidateImage(this, CacheManager.getImagePath(RegisterFragment.CACHE_AVATAR));
            if(listener!= null) {
                listener.setAvatar(true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    interface OnChangeAvatarListener {
        void setAvatar(boolean isAvatar);
    }

    public void createAccount(){

        final Bundle data = new Bundle();
        data.putString(AccountManager.KEY_ACCOUNT_NAME, String.valueOf(AuthManager.getAuthToken().getUserId()));
        data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        data.putString(AccountManager.KEY_AUTHTOKEN, AuthManager.getAuthToken().getHashKey());

        final Intent result = new Intent();
        result.putExtras(data);

        setResult(RESULT_OK, result);
        finish();
    }
}

