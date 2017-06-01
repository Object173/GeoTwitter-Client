package com.object173.geotwitter.gui.main.dialogs;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.Dialog;
import com.object173.geotwitter.database.entities.Message;
import com.object173.geotwitter.database.entities.Profile;
import com.object173.geotwitter.database.service.DialogService;
import com.object173.geotwitter.database.service.MessageService;
import com.object173.geotwitter.gui.base.CursorRecyclerViewAdapter;
import com.object173.geotwitter.gui.views.CircleImageView;
import com.object173.geotwitter.util.resources.ImagesManager;

public final class DialogsAdapter extends CursorRecyclerViewAdapter<DialogsAdapter.ViewHolder> {

    private final OnItemClickListener listener;

    public DialogsAdapter(final Cursor cursor, final OnItemClickListener listener) {
        super(cursor);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_dialogs_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.bind(cursor, listener);
    }

    public interface OnItemClickListener {
        void onProfileClick(Profile item);
        void onDialogClick(Dialog item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView avatarView;
        final TextView usernameView;
        final TextView messageView;
        final TextView dateView;
        final TextView unreadCountView;

        RelativeLayout contentButton;

        ViewHolder(final View view) {
            super(view);

            avatarView = (CircleImageView) view.findViewById(R.id.avatar_view);
            usernameView = (TextView) view.findViewById(R.id.username_field);
            messageView = (TextView) view.findViewById(R.id.message_field);
            dateView = (TextView) view.findViewById(R.id.date_field);
            unreadCountView = (TextView) view.findViewById(R.id.unread_count_field);

            contentButton = (RelativeLayout) view.findViewById(R.id.content_field);
        }

        void bind(final Cursor cursor, final OnItemClickListener listener) {
            final Context context = avatarView.getContext();
            final Dialog dialog = DialogService.getDialog(context, cursor);
            final Profile profile = dialog.getCompanion();
            final Message lastMessage = MessageService.getLastMessage(context, dialog.getId());
            final int unreadMessageCount = MessageService.getUnreadCount(context, dialog.getId());

            if(unreadMessageCount > 0) {
                unreadCountView.setVisibility(View.VISIBLE);
                unreadCountView.setText("+ " + unreadMessageCount);
            }
            else {
                unreadCountView.setVisibility(View.GONE);
            }

            if(profile.getAvatarMini() != null) {
                ImagesManager.setImageViewCache(context, avatarView, R.mipmap.avatar,
                        profile.getAvatarMini());
            }
            else {
                avatarView.setImageResource(R.mipmap.avatar);
            }
            usernameView.setText(profile.getUsername());

            if(lastMessage != null) {
                if(lastMessage.getImage() != null) {
                    messageView.setText(R.string.dialog_activity_message_image);
                } else
                if(lastMessage.getMarker() != null) {
                    messageView.setText(R.string.dialog_activity_message_marker);
                }
                else {
                    messageView.setText(lastMessage.getText());
                }
                dateView.setText(lastMessage.getDateString());
            }
            else {
                messageView.setText(R.string.message_empty_dialog_message);
                dateView.setText(R.string.message_empty_dialog_date);
            }

            contentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onDialogClick(dialog);
                    }
                }
            });

            avatarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        listener.onProfileClick(profile);
                    }
                }
            });
        }
    }
}
