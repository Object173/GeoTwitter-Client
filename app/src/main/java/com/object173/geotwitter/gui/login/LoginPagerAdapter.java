package com.object173.geotwitter.gui.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LoginPagerAdapter extends FragmentPagerAdapter {

    private enum Pages {
        SignIn
                {
                    @Override
                    Fragment getPage() {
                        return new SignInFragment();
                    }
                },
        Register
                {
                    @Override
                    Fragment getPage() {
                        return new RegisterFragment();
                    }
                };

        Fragment getPage() {
            return null;
        }
    }

    private final List<String> pagesTitles = new ArrayList<>();

    public LoginPagerAdapter(final FragmentManager fm, final String[] titles) {
        super(fm);
        Collections.addAll(pagesTitles, titles);
    }

    @Override
    public Fragment getItem(final int position) {
        return Pages.values()[position].getPage();
    }

    @Override
    public int getCount() {
        if(pagesTitles==null) {
            return 0;
        }
        return pagesTitles.size();
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return pagesTitles.get(position);
    }
}
