package com.object173.geotwitter.gui.login;

import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.gui.util.DeleteImageActivity;
import com.object173.geotwitter.service.authorization.AuthAccount;
import com.object173.geotwitter.util.resources.CacheManager;
import com.object173.geotwitter.util.resources.ChooserManager;
import com.object173.geotwitter.util.resources.ImagesManager;
import com.object173.geotwitter.util.user.AuthManager;
import com.soundcloud.android.crop.Crop;

public class LoginActivity extends MyBaseActivity {

    public final static String PARAM_USER_PASS = "USER_PASS";

    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mResultBundle = null;

    private AccountManager mAccountManager;
    private String mAuthTokenType;

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_TOKEN_TYPE = "TOKEN_TYPE";

    private OnChangeAvatarListener listener;

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_login, null);

        mAccountAuthenticatorResponse =
                getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }

        mAccountManager = AccountManager.get(getBaseContext());

        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null) {
            mAuthTokenType = AuthAccount.TOKEN_FULL_ACCESS;
        }

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
        //finishSignIn();
    }

    public void finishSignIn() {
        if(AuthManager.isAuth()) {
            hideProgressDialog();

            setResult(RESULT_OK);
            finish();
        }
    }

    public final void setListener(final OnChangeAvatarListener listener) {
        this.listener = listener;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if(requestCode == ChooserManager.INTENT_PICK_IMAGE) {
            if(resultCode == RESULT_OK) {
                final Uri uri = ChooserManager.getImageUri(this, data);
                ChooserManager.selectImage(this, uri, CacheManager.getImagePath(RegisterFragment.CACHE_AVATAR));
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

    @Override
    public void finish() {
        if (mAccountAuthenticatorResponse != null) {
            if (mResultBundle != null) {
                mAccountAuthenticatorResponse.onResult(mResultBundle);
            } else {
                mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED, "canceled");
            }
            mAccountAuthenticatorResponse = null;
        }
        super.finish();
    }
}

