package com.object173.geotwitter.gui.main.contacts;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.Profile;
import com.object173.geotwitter.database.service.ProfileService;
import com.object173.geotwitter.gui.base.CursorRecyclerViewAdapter;
import com.object173.geotwitter.gui.views.CircleImageView;
import com.object173.geotwitter.server.json.AuthProfile;
import com.object173.geotwitter.util.resources.ImagesManager;

public final class ContactsAdapter extends CursorRecyclerViewAdapter<ContactsAdapter.ViewHolder> {

    private final OnItemClickListener listener;

    public ContactsAdapter(final Cursor cursor, final OnItemClickListener listener) {
        super(cursor);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_contacts_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.bind(cursor, listener);
    }

    public interface OnItemClickListener {
        void onProfileClick(Profile item);
        void onAddClick(Profile item);
        void onRemoveClick(Profile item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView avatarView;
        TextView usernameView;
        TextView statusView;

        ImageView addButton;
        RelativeLayout profileButton;

        ViewHolder(final View view) {
            super(view);

            avatarView = (CircleImageView) view.findViewById(R.id.avatar_view);
            usernameView = (TextView) view.findViewById(R.id.username_field);
            statusView = (TextView) view.findViewById(R.id.status_field);

            addButton = (ImageView) view.findViewById(R.id.add_button);
            profileButton = (RelativeLayout) view.findViewById(R.id.profile_button);
        }

        void bind(final Cursor cursor, final OnItemClickListener listener) {
            final Profile profile = ProfileService.getContact(avatarView.getContext(), cursor);

            if(profile.getAvatarMini() != null) {
                ImagesManager.setImageViewCache(avatarView.getContext(), avatarView,
                        R.mipmap.avatar, profile.getAvatarMini());
            }
            else {
                avatarView.setImageResource(R.mipmap.avatar);
            }
            usernameView.setText(profile.getUsername());
            statusView.setText(profile.getStatus());

            profileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onProfileClick(profile);
                    }
                }
            });

            if(profile.getRelationStatus().equals(AuthProfile.RelationStatus.NONE) ||
                    profile.getRelationStatus().equals(AuthProfile.RelationStatus.SUBSCRIBER)) {
                addButton.setImageResource(R.mipmap.ic_add_contact_black);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(listener != null) {
                            listener.onAddClick(profile);
                        }
                    }
                });
            }
            else {
                addButton.setImageResource(R.mipmap.ic_account_minus_black);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(listener != null) {
                            listener.onRemoveClick(profile);
                        }
                    }
                });
            }
        }
    }
}
