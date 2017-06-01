package com.object173.geotwitter.gui.contacts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.views.CircleImageView;
import com.object173.geotwitter.server.json.AuthProfile;
import com.object173.geotwitter.util.resources.ImagesManager;

import java.util.ArrayList;
import java.util.List;

public final class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private final List<AuthProfile> contactList = new ArrayList<>();
    private final OnItemClickListener listener;

    public ContactsAdapter(final OnItemClickListener listener) {
        this.listener = listener;
    }

    public ContactsAdapter(final OnItemClickListener listener, final List<AuthProfile> contactList) {
        if(contactList != null) {
            this.contactList.addAll(contactList);
        }
        this.listener = listener;
    }

    public List<AuthProfile> getData() {
        return contactList;
    }

    public final void addList(final List<AuthProfile> contactList) {
        if(contactList != null) {
            this.contactList.addAll(contactList);
            notifyDataSetChanged();
        }
    }

    public final void clearList() {
        this.contactList.clear();
        notifyDataSetChanged();
    }

    public final void replace(final long id, final AuthProfile.RelationStatus relation) {
        for(int i = 0; i < contactList.size(); i++) {
            if(contactList.get(i).getUserId() == id) {
                contactList.get(i).setRelation(relation);
                notifyDataSetChanged();
                return;
            }
        }
    }

    public final AuthProfile getItem(final int position) {
        return contactList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_contacts_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bind(getItem(position), listener);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public interface OnItemClickListener {
        void onProfileClick(AuthProfile item);
        void onAddClick(AuthProfile item);
        void onRemoveClick(AuthProfile item);
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

        void bind(final AuthProfile profile, final OnItemClickListener listener) {
            if(profile.getAvatarUrlMini() != null) {
                ImagesManager.setImageViewOnline(avatarView.getContext(), avatarView,
                        R.mipmap.avatar, profile.getAvatarUrlMini());
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

            if(profile.getRelation().equals(AuthProfile.RelationStatus.NONE) ||
                    profile.getRelation().equals(AuthProfile.RelationStatus.SUBSCRIBER)) {
                addButton.setVisibility(View.VISIBLE);
                addButton.setImageResource(R.mipmap.ic_add_contact_black);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(listener != null) {
                            listener.onAddClick(profile);
                        }
                    }
                });
            } else
            if(profile.getRelation().equals(AuthProfile.RelationStatus.INVITE)) {
                addButton.setVisibility(View.VISIBLE);
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
            else {
                addButton.setVisibility(View.GONE);
                addButton.setOnClickListener(null);
            }
        }
    }
}
