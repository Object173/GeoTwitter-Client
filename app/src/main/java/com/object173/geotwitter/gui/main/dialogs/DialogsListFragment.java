package com.object173.geotwitter.gui.main.dialogs;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.Dialog;
import com.object173.geotwitter.database.entities.Profile;
import com.object173.geotwitter.database.service.DialogService;
import com.object173.geotwitter.gui.base.MyBaseFragmentList;
import com.object173.geotwitter.gui.messenger.DialogActivity;
import com.object173.geotwitter.gui.profile.ProfileActivity;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.service.contacts.InviteTask;
import com.object173.geotwitter.service.messenger.LoadDialogsTask;

public final class DialogsListFragment extends MyBaseFragmentList
        implements DialogsAdapter.OnItemClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_dialogs_list, null);

        final Cursor cursor = DialogService.getDialogList(getContext());
        if(cursor != null) {
            getActivity().startManagingCursor(cursor);
        }

        final DialogsAdapter adapter = new DialogsAdapter(cursor, this);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        initRecyclerView(recyclerView, adapter);

        return view;
    }

    @Override
    public void onProfileClick(Profile item) {
        if(item != null) {
            ProfileActivity.startActivity(getContext(), item.getUserId());
        }
    }

    @Override
    public void onDialogClick(Dialog item) {
        if(item != null) {
            DialogActivity.startActivity(getContext(), item.getId());
        }
    }

    @Override
    public void receiveMessage(Intent intent) {
        if(LoadDialogsTask.isGetIntent(intent)) {
            final AuthResult.Result result = InviteTask.getAuthResult(intent);
            showReceiveResult(result);
        }
    }
}
