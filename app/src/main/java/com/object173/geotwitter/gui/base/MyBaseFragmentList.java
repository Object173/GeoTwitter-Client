package com.object173.geotwitter.gui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.gui.util.ServiceConnectionController;

public abstract class MyBaseFragmentList extends MyBaseFragment
        implements ServiceConnectionController.ServiceConnector{

    private ServiceConnectionController connectionController = new ServiceConnectionController();

    private LinearLayoutManager layoutManager;
    private int scrollPosition = 0;
    private static final String KEY_SCROLL_POSITION = "scroll_position";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionController.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            scrollPosition = savedInstanceState.getInt(KEY_SCROLL_POSITION);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        connectionController.onResume(getContext(), this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(layoutManager != null) {
            outState.putInt(KEY_SCROLL_POSITION, layoutManager.findFirstVisibleItemPosition());
        }
        connectionController.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        connectionController.onPause(getContext());
    }

    protected void initRecyclerView(final RecyclerView recyclerView, final RecyclerView.Adapter adapter) {
        if(recyclerView == null || adapter == null) {
            return;
        }
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(scrollPosition);
    }

    protected boolean setRequestId(final long requestId) {
        if(requestId > DatabaseContract.NULL_ID) {
            return connectionController.setRequestId(requestId);
        }
        return false;
    }

    @Override
    public void receiveMessage(Intent intent) {
    }

    @Override
    public void finishTask(Intent intent) {
    }

    @Override
    public void finishTask(Class serviceClass) {
    }
}
