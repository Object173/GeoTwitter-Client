package com.object173.geotwitter.gui.main.news;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.object173.geotwitter.R;
import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.entities.NewPlace;
import com.object173.geotwitter.database.entities.Profile;
import com.object173.geotwitter.database.service.PlaceService;
import com.object173.geotwitter.gui.base.MyBaseFragmentList;
import com.object173.geotwitter.gui.place.PlaceActivity;
import com.object173.geotwitter.gui.profile.ProfileActivity;
import com.object173.geotwitter.gui.util.RefreshRecyclerListener;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.service.place.LoadPlaceTask;
import com.object173.geotwitter.service.place.NewsPlaceService;

public final class PlaceListFragment extends MyBaseFragmentList
        implements PlaceAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "PlaceListFragment";

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    private PlaceAdapter adapter;

    private long authorId = DatabaseContract.NULL_ID;
    private static final String KEY_AUTHOR_ID = "author_id";

    private boolean isLoad = false;
    private static final String KEY_IS_LOAD = "is_load";

    private final RefreshRecyclerListener refreshRecyclerListener = new RefreshRecyclerListener() {
        @Override
        public void onLoadMore(String request, int size, int offset) {
            if(getContext() != null) {
                NewsPlaceService.startToLoadBottomPlace(getContext(), authorId, size, offset);
            }
        }
    };

    public static PlaceListFragment newInstance(final long authorId) {
        if(authorId <= DatabaseContract.NULL_ID) {
            return null;
        }

        final PlaceListFragment fragment = new PlaceListFragment();
        final Bundle bundle = new Bundle();
        bundle.putLong(KEY_AUTHOR_ID, authorId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_place_list, null);
        Log.d("PlaceListFragment", "onCreateView");

        if(getArguments() != null) {
            authorId = getArguments().getLong(KEY_AUTHOR_ID, DatabaseContract.NULL_ID);
        }

        if(savedInstanceState != null) {
            isLoad = savedInstanceState.getBoolean(KEY_IS_LOAD, false);
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        refreshRecyclerListener.onCreate(savedInstanceState);

        initRecyclerView();

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setRefreshing(isLoad);
        refreshLayout.setOnRefreshListener(this);

        return view;
    }

    private void initRecyclerView() {
        Log.d("PlaceListFragment", "initRecyclerView");
        if(recyclerView == null) {
            return;
        }
        final Cursor cursor = PlaceService.getPlaceList(getContext(), authorId);
        if(cursor == null) {
            return;
        }
        getActivity().startManagingCursor(cursor);

        adapter = new PlaceAdapter(getContext(), cursor, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshRecyclerListener.setRecyclerView(getContext(), recyclerView, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("PlaceListFragment", "onResume");
        if(recyclerView.getAdapter() == null) {
            initRecyclerView();
        }
        if(adapter != null) {
            adapter.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(adapter != null) {
            adapter.onPause();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        refreshRecyclerListener.saveInstanceState(outState);
        outState.putBoolean(KEY_IS_LOAD, refreshLayout.isRefreshing());
    }

    @Override
    public void onProfileClick(Profile item) {
        if(item != null) {
            ProfileActivity.startActivity(getContext(), item.getUserId());
        }
    }

    @Override
    public void onPlaceClick(NewPlace item) {
        if(item != null) {
            PlaceActivity.startActivity(getContext(), item);
        }
    }

    @Override
    public void receiveMessage(Intent intent) {
        super.receiveMessage(intent);
        if(LoadPlaceTask.isLoadBottomIntent(intent)) {
            final AuthResult.Result result = LoadPlaceTask.getAuthResult(intent);
            showReceiveResult(result);
        }
    }

    @Override
    public void finishTask(Intent intent) {
        super.finishTask(intent);
        if(LoadPlaceTask.isLoadTopIntent(intent)) {
            final AuthResult.Result result = LoadPlaceTask.getAuthResult(intent);
            showReceiveResult(result);
            refreshLayout.setRefreshing(false);
            refreshRecyclerListener.setPosition(0);
        }
    }

    @Override
    public void finishTask(Class serviceClass) {
        super.finishTask(serviceClass);
        refreshLayout.setRefreshing(false);
        refreshRecyclerListener.setPosition(0);
    }

    @Override
    public void onRefresh() {
        if(refreshLayout.isRefreshing() &&
                setRequestId(NewsPlaceService.startToLoadTopPlace(getContext(), authorId))) {
            refreshLayout.setRefreshing(true);
        }
    }
}
