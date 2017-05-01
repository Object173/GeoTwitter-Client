package com.object173.geotwitter.gui.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Object173
 * on 27.04.2017.
 */

final class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    private ViewPagerAdapter(final FragmentManager fm) {
        super(fm);
    }

    static ViewPagerAdapter newInstance(final FragmentManager fm, final Fragment[] fragments,
                                               final String[] titles) {
        if(fm == null || fragments == null || titles == null) {
            return null;
        }
        if(fragments.length != titles.length) {
            return null;
        }

        final ViewPagerAdapter adapter = new ViewPagerAdapter(fm);
        for(int i = 0; i < fragments.length; i++) {
            adapter.addFragment(fragments[i], titles[i]);
        }

        return adapter;
    }

    @Override
    public Fragment getItem(final int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return mFragmentTitleList.get(position);
    }

    private void addFragment(final Fragment fragment, final String title) {
        if(fragment != null && title != null) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }
}
