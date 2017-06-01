package com.object173.geotwitter.gui.place;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.object173.geotwitter.R;
import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.gui.main.news.PlaceListFragment;

public class PlaceListActivity extends MyBaseActivity {

    private long authorId = DatabaseContract.NULL_ID;
    private static final String KEY_AUTHOR_ID = "author_id";

    public static boolean startActivity(final Context context, final long authorId) {
        if(context == null) {
            return false;
        }
        final Intent intent = new Intent(context, PlaceListActivity.class);
        intent.putExtra(KEY_AUTHOR_ID, authorId);
        context.startActivity(intent);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!super.onCreate(savedInstanceState, R.layout.activity_place_list, true)) {
            return;
        }
        authorId = getIntent().getExtras().getLong(KEY_AUTHOR_ID, DatabaseContract.NULL_ID);

        final FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(PlaceListFragment.TAG);

        if(fragment == null) {
            fragment = PlaceListFragment.newInstance(authorId);
            fm.beginTransaction().add(R.id.frame_layout, fragment, PlaceListFragment.TAG).commit();
        }
    }
}
