package com.object173.geotwitter.gui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.object173.geotwitter.gui.main.contacts.ContactsListFragment;
import com.object173.geotwitter.gui.main.dialogs.DialogsListFragment;
import com.object173.geotwitter.gui.main.news.PlaceListFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MainPagerAdapter extends FragmentPagerAdapter {

    private enum Pages {
        Places
                {
                    @Override
                    Fragment getPage() {
                        return new PlaceListFragment();
                    }
                },
        Dialogs
                {
                    @Override
                    Fragment getPage() {
                        return new DialogsListFragment();
                    }
                },
        Contacts
                {
                    @Override
                    Fragment getPage() {
                        return new ContactsListFragment();
                }
                };

        Fragment getPage() {
            return null;
        }
    }

    private final List<String> pagesTitles = new ArrayList<>();

    public MainPagerAdapter(final FragmentManager fm, final String[] titles) {
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
