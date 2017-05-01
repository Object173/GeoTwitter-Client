package com.object173.geotwitter.gui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.DeleteAvatarActivity;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.gui.main.MainActivity;
import com.object173.geotwitter.util.AvatarManager;
import com.object173.geotwitter.util.ChooseImageManager;
import com.soundcloud.android.crop.Crop;

public class LoginActivity extends MyBaseActivity{

    private final  RegisterFragment regFragment = new RegisterFragment();
    private final Fragment[] pagerFragments = {new SignInFragment(), regFragment};

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_login, null);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.login_activity_viewpager);
        final ViewPagerAdapter adapter = ViewPagerAdapter.newInstance(getSupportFragmentManager(),
                pagerFragments, new String[] {getString(R.string.sign_in_fragment_title),
                                                getString(R.string.register_fragment_title)});
        viewPager.setAdapter(adapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.login_activity_tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //finishSignIn();
    }

    public void finishSignIn() {
        hideProgressDialog();

        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if(requestCode == RegisterFragment.INTENT_PICK_IMAGE) {
            if(resultCode == RESULT_OK) {
                final Uri uri = ChooseImageManager.getImageUri(this, data);
                AvatarManager.selectAvatar(this, uri);
            }
            else
            if(resultCode == DeleteAvatarActivity.RESULT_ACTIVITY) {
                regFragment.setAvatar(false);
            }
        } else
        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK && data!= null) {
            regFragment.setAvatar(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

