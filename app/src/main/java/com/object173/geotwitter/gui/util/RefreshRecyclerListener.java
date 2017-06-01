package com.object173.geotwitter.gui.util;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;

import com.object173.geotwitter.R;
import com.object173.geotwitter.util.PreferencesManager;

public abstract class RefreshRecyclerListener extends RecyclerView.OnScrollListener
        implements SearchView.OnQueryTextListener{

    private int previousTotal = 0;
    private int position = 0;
    private boolean loading = false;

    private int blockSize;

    private String request = "";

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView recyclerView;

    private static final String KEY_REQUEST = "request";
    private static final String KEY_COUNT = "count";
    private static final String KEY_POSITION = "position";

    public void onCreate(final Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            request = savedInstanceState.getString(KEY_REQUEST);
            previousTotal = savedInstanceState.getInt(KEY_COUNT);
            position = savedInstanceState.getInt(KEY_POSITION);
        }
    }

    public void setRecyclerView(final Context context, final RecyclerView recyclerView, final boolean isEndToLoad) {
        if(context == null || recyclerView == null) {
            return;
        }
        this.recyclerView = recyclerView;

        blockSize = PreferencesManager.getIntPreference(context,
                context.getString(R.string.key_preference_block_size),
                context.getResources().getInteger(R.integer.preference_block_size_default));

        if(recyclerView.getAdapter() != null) {
            previousTotal = recyclerView.getAdapter().getItemCount();
        }
        if(previousTotal == 0) {
            onLoadMore(request, blockSize, 0);
            loading = true;
        }

        this.mLinearLayoutManager = new LinearLayoutManager(context);
        if(!isEndToLoad) {
            this.mLinearLayoutManager.setStackFromEnd(true);
            this.mLinearLayoutManager.setReverseLayout(true);
        }
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.addOnScrollListener(this);
        recyclerView.scrollToPosition(position);
    }

    public void setSearchView(final MenuItem searchItem) {
        if(searchItem == null) {
            return;
        }
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        if (request != null && !request.isEmpty()) {
            searchItem.expandActionView();
            searchView.setQuery(request, true);
            searchView.clearFocus();
        }
        searchView.setOnQueryTextListener(RefreshRecyclerListener.this);
    }

    public void saveInstanceState(final Bundle outState) {
        if(outState != null) {
            outState.putInt(KEY_COUNT, previousTotal);
            outState.putString(KEY_REQUEST, request);
            if(mLinearLayoutManager != null) {
                outState.putInt(KEY_COUNT, mLinearLayoutManager.findFirstVisibleItemPosition());
            }
        }
        if(recyclerView != null) {
            recyclerView.removeOnScrollListener(this);
        }
    }

    public final void setPosition(final int position) {
        Log.d("RefreshRecyclerListener", "setPosition");
        this.position = position;
        if(recyclerView != null) {
            recyclerView.scrollToPosition(position);
        }
    }

    public final void setRequest(final String request) {
        this.request = request;
        reload();
    }

    public final void reload() {
        previousTotal = 0;
        loading = true;
        onLoadMore(request, blockSize, 0);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        final int visibleItemCount = recyclerView.getChildCount();
        final int totalItemCount = mLinearLayoutManager.getItemCount();
        final int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading && totalItemCount != previousTotal) {
            loading = false;
            previousTotal = totalItemCount;
        }

        if (!loading && (totalItemCount - visibleItemCount <= firstVisibleItem)) {
            Log.d("RefreshRecyclerView","loadMore");
            onLoadMore(request, blockSize, totalItemCount);
            loading = true;
        }
    }

    public abstract void onLoadMore(String request, int size, int offset);

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        setRequest(newText);
        return true;
    }
}
