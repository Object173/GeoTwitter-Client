package com.object173.geotwitter.gui.choose;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.object173.geotwitter.R;
import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.entities.Profile;
import com.object173.geotwitter.database.service.ProfileService;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.gui.profile.ProfileActivity;

public class ChooseContactActivity extends MyBaseActivity
        implements ContactsAdapter.OnItemClickListener{

    public static final int REQUEST_CODE = 101;

    private LinearLayoutManager layoutManager;
    private int scrollPosition;
    private static final String KEY_SCROLL_POSITION = "scroll_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!super.onCreate(savedInstanceState, R.layout.activity_choose_contact, true)) {
            finish();
            return;
        }

        if(savedInstanceState != null) {
            scrollPosition = savedInstanceState.getInt(KEY_SCROLL_POSITION);
        }

        final Cursor cursor = ProfileService.getRequireContactList(this);
        startManagingCursor(cursor);

        final ContactsAdapter adapter = new ContactsAdapter(cursor, this);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCROLL_POSITION, layoutManager.findFirstVisibleItemPosition());
    }

    @Override
    public void onProfileClick(Profile item) {
        if(item != null) {
            ProfileActivity.startActivity(this, item.getUserId());
        }
    }

    private static final String KEY_CONTACT_ID = "contact_id";

    @Override
    public void onAddClick(Profile item) {
        if(item != null) {
            final Intent intent = new Intent();
            intent.putExtra(KEY_CONTACT_ID, item.getUserId());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public static long getContactId(final Intent intent) {
        if(intent == null) {
            return DatabaseContract.NULL_ID;
        }
        return intent.getLongExtra(KEY_CONTACT_ID, DatabaseContract.NULL_ID);
    }
}
