package com.object173.geotwitter.gui.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.gui.profile.ProfileActivity;
import com.object173.geotwitter.gui.util.FrameController;
import com.object173.geotwitter.gui.util.RefreshRecyclerListener;
import com.object173.geotwitter.gui.util.ServiceConnectionController;
import com.object173.geotwitter.server.json.AuthProfile;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.service.contacts.ContactsService;
import com.object173.geotwitter.service.contacts.InviteTask;
import com.object173.geotwitter.util.PreferencesManager;

import java.util.List;

public class AddContactActivity extends MyBaseActivity
        implements ContactsAdapter.OnItemClickListener,
                    LoaderManager.LoaderCallbacks<List<AuthProfile>>,
                    ServiceConnectionController.ServiceConnector{

    private static final int LOADER_ID = 17;

    private ContactsAdapter adapter;

    private final ServiceConnectionController controller = new ServiceConnectionController();

    private static final int FRAME_LOAD = 1;
    private static final int FRAME_CONTENT = 2;

    private final FrameController frameController =
            new FrameController(R.id.frame_layout, new FrameController.Frame[]
                    {
                            new FrameController.Frame(FRAME_LOAD, R.id.progress_bar),
                            new FrameController.Frame(FRAME_CONTENT, R.id.recycler_view)
                    });

    private final RefreshRecyclerListener refreshRecyclerListener = new RefreshRecyclerListener() {
        @Override
        public void onLoadMore(String request, int size, int offset) {
            loadData(request, size, offset);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!super.onCreate(savedInstanceState, R.layout.activity_add_contact, true)) {
            finish();
            return;
        }
        Log.d("AddContactActivity","onCreate");

        final int blockSize = PreferencesManager.getIntPreference(this,
                getString(R.string.key_preference_block_size),
                getResources().getInteger(R.integer.preference_block_size_default));

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final List<AuthProfile> profileList = (List<AuthProfile>)getLastCustomNonConfigurationInstance();
        adapter = new ContactsAdapter(this, profileList);
        recyclerView.setAdapter(adapter);

        refreshRecyclerListener.onCreate(savedInstanceState);
        refreshRecyclerListener.setRecyclerView(this, recyclerView, true);

        controller.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.onResume(this, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        refreshRecyclerListener.saveInstanceState(outState);
        controller.onSaveInstanceState(outState);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return adapter.getData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        controller.onPause(this);
    }

    private void loadData(final String request, final int blockSize, final int offset) {
        Log.d("AddContactActivity",request + " " + blockSize + " " + offset);
        if(offset == 0) {
            adapter.clearList();
            frameController.setViewState(findViewById(R.id.frame_layout), FRAME_LOAD);
        }
        final Loader<List<AuthProfile>> loader = getSupportLoaderManager().restartLoader(LOADER_ID,
                ContactsLoader.newInstance(request, blockSize, offset), this);
        loader.forceLoad();
    }

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        refreshRecyclerListener.setSearchView(searchItem);

        return true;
    }

    @Override
    public void onProfileClick(final AuthProfile item) {
        if(item != null) {
            ProfileActivity.startActivity(this, item);
        }
    }

    @Override
    public void onAddClick(final AuthProfile item) {
        if(item != null) {
            ContactsService.startToSendInvite(this, item.getUserId());
        }
    }

    @Override
    public void onRemoveClick(AuthProfile item) {
        if(item != null) {
            ContactsService.startToRemoveInvite(this, item.getUserId());
        }
    }

    @Override
    public Loader<List<AuthProfile>> onCreateLoader(int id, Bundle args) {
        Log.d("AddContactActivity","onCreateLoader");
        return new ContactsLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<List<AuthProfile>> loader, List<AuthProfile> data) {
        Log.d("AddContactActivity","onLoadFinished");
        adapter.addList(data);
        frameController.setViewState(findViewById(R.id.frame_layout), FRAME_CONTENT);
    }

    @Override
    public void onLoaderReset(Loader<List<AuthProfile>> loader) {
    }

    @Override
    public void receiveMessage(final Intent intent) {
        Log.d("AddContactActivity","receiveMessage");
        if(InviteTask.isThisIntent(intent)) {
            final AuthResult.Result result = InviteTask.getAuthResult(intent);

            if(result.equals(AuthResult.Result.SUCCESS)) {
                if(InviteTask.isAddInvite(intent)) {
                    if(adapter != null) {
                        adapter.replace(InviteTask.getContactId(intent), InviteTask.getRelation(intent));
                    }
                    showSnackbar(R.string.message_success_send_invite, false);
                } else
                if(InviteTask.isRemoveInvite(intent)) {
                    if(adapter != null) {
                        adapter.replace(InviteTask.getContactId(intent), InviteTask.getRelation(intent));
                    }
                    showSnackbar(R.string.message_success_remove_invite, false);
                }
            } else
            if(result.equals(AuthResult.Result.NO_INTERNET)) {
                showSnackbar(R.string.login_activity_message_no_internet, false);
            } else
            if(result.equals(AuthResult.Result.FAIL)) {
                showSnackbar(R.string.login_activity_message_error_server, false);
            }
        }
    }

    @Override
    public void finishTask(Intent intent) {
        receiveMessage(intent);
    }

    @Override
    public void finishTask(Class serviceClass) {
    }
}
