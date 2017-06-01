package com.object173.geotwitter.gui.main.contacts;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.Profile;
import com.object173.geotwitter.database.service.ProfileService;
import com.object173.geotwitter.gui.base.MyBaseFragmentList;
import com.object173.geotwitter.gui.profile.ProfileActivity;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.service.contacts.ContactsService;
import com.object173.geotwitter.service.contacts.InviteTask;

public final class ContactsListFragment extends MyBaseFragmentList
        implements ContactsAdapter.OnItemClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_contacts_list, null);

        final Cursor cursor = ProfileService.getContactList(getContext());
        if(cursor != null) {
            getActivity().startManagingCursor(cursor);
        }

        final ContactsAdapter adapter = new ContactsAdapter(cursor, this);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        initRecyclerView(recyclerView, adapter);

        return view;
    }

    @Override
    public void onProfileClick(final Profile item) {
        if(item != null) {
            ProfileActivity.startActivity(getContext(), item.getUserId());
        }
    }

    @Override
    public void onAddClick(Profile item) {
        ContactsService.startToSendInvite(getContext(), item.getUserId());
    }

    @Override
    public void onRemoveClick(Profile item) {
        ContactsService.startToRemoveInvite(getContext(), item.getUserId());
    }

    @Override
    public void receiveMessage(Intent intent) {
        if(InviteTask.isThisIntent(intent)) {
            final AuthResult.Result result = InviteTask.getAuthResult(intent);
            showReceiveResult(result);
        }
    }
}
