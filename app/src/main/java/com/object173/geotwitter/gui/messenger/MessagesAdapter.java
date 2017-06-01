package com.object173.geotwitter.gui.messenger;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.Marker;
import com.object173.geotwitter.database.entities.Message;
import com.object173.geotwitter.database.service.MessageService;
import com.object173.geotwitter.gui.base.CursorRecyclerViewAdapter;
import com.object173.geotwitter.gui.images.ImageViewerActivity;
import com.object173.geotwitter.gui.util.MapHelper;
import com.object173.geotwitter.service.messenger.MessengerService;
import com.object173.geotwitter.util.BufferManager;
import com.object173.geotwitter.util.resources.ImagesManager;
import com.object173.geotwitter.util.user.AuthManager;

public final class MessagesAdapter extends CursorRecyclerViewAdapter<MessagesAdapter.ViewHolder> {

    public MessagesAdapter(final Cursor cursor) {
        super(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.bind(cursor);
    }

    public interface OnItemClickListener {
        void onItemClick(Message item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener,
                        MenuItem.OnMenuItemClickListener,
                        OnMapReadyCallback {

        final LinearLayout layout;
        final CardView cardView;

        final ImageView statusIcon;
        final TextView messageField;
        final TextView dateField;

        final ImageView imageView;

        final MapView mapView;
        GoogleMap map;

        Marker marker = null;
        long localId = 0;
        int status = 0;

        ViewHolder(final View view) {
            super(view);

            layout = (LinearLayout) view.findViewById(R.id.message_layout);
            cardView = (CardView) view.findViewById(R.id.card_view);

            statusIcon = (ImageView) view.findViewById(R.id.status_icon);
            messageField = (TextView) view.findViewById(R.id.message_field);
            dateField = (TextView) view.findViewById(R.id.date_field);

            imageView = (ImageView) view.findViewById(R.id.image_view);

            mapView = (MapView) view.findViewById(R.id.map);

            cardView.setOnCreateContextMenuListener(this);
        }

        void bind(final Cursor cursor) {
            final Context context = statusIcon.getContext();
            final Message message = MessageService.getMessage(context, cursor);

            if(message.getImage() != null) {
                imageView.setVisibility(View.VISIBLE);
                ImagesManager.setImageViewCache(context, imageView, R.mipmap.ic_image, message.getImage());

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImageViewerActivity.startActivity(context, message.getImage(), null);
                    }
                });
            }
            else {
                imageView.setVisibility(View.GONE);
            }

            if(message.getMarker() != null) {
                marker = message.getMarker();
                mapView.onCreate(null);
                mapView.getMapAsync(this);
                mapView.setVisibility(View.VISIBLE);
            }
            else {
                mapView.setVisibility(View.GONE);
                map = null;
                marker = null;
            }

            if(message.getText() == null || message.getText().length() <= 0) {
                messageField.setVisibility(View.GONE);
            }
            else {
                messageField.setVisibility(View.VISIBLE);
                messageField.setText(message.getText());
            }

            dateField.setText(message.getDateTimeString());

            localId = message.getLocalId();
            status = message.getStatus();

            switch (message.getStatus()) {
                case Message.STATUS_WAIT:
                    statusIcon.setVisibility(View.VISIBLE);
                    statusIcon.setImageResource(R.mipmap.ic_clock_black);
                    break;
                case Message.STATUS_FAIL:
                    statusIcon.setVisibility(View.VISIBLE);
                    statusIcon.setImageResource(R.mipmap.ic_report_problem);
                    break;
                case Message.STATUS_UNREAD:
                    statusIcon.setVisibility(View.GONE);
                    message.setStatus(Message.STATUS_SUCCESS);
                    MessageService.updateMessage(context, message);
                    break;
                default:
                    statusIcon.setVisibility(View.GONE);
                    break;
            }

            if(message.getSenderId() == AuthManager.getAuthToken().getUserId()) {
                layout.setGravity(Gravity.RIGHT);
                cardView.setCardBackgroundColor(context.getResources().getColor(R.color.message_card_user_background));
            }
            else {
                layout.setGravity(Gravity.LEFT);
                cardView.setCardBackgroundColor(context.getResources().getColor(R.color.message_card_companion_background));
            }
        }

        private static final int MENU_COPY_ID = 1;
        private static final int MENU_RESEND_ID = 2;
        private static final int MENU_CANCEL_ID = 3;

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            final MenuItem Copy = contextMenu.add(Menu.NONE, 1, MENU_COPY_ID, R.string.menu_title_item_copy);
            Copy.setOnMenuItemClickListener(this);

            if(status == Message.STATUS_FAIL) {
                final MenuItem Resend = contextMenu.add(Menu.NONE, 2, MENU_RESEND_ID, R.string.menu_title_item_resend);
                Resend.setOnMenuItemClickListener(this);
                final MenuItem Cancel = contextMenu.add(Menu.NONE, 3, MENU_CANCEL_ID, R.string.menu_title_item_cancel);
                Cancel.setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(menuItem == null) {
                return false;
            }
            switch (menuItem.getItemId()) {
                case MENU_COPY_ID:
                    BufferManager.copyText(statusIcon.getContext(), messageField.getText().toString());
                    return true;
                case MENU_RESEND_ID:
                    MessengerService.startToResendMessage(statusIcon.getContext(), localId);
                    return true;
                case MENU_CANCEL_ID:
                    MessageService.cancelMessage(statusIcon.getContext(), localId);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onMapReady(final GoogleMap googleMap) {
            if(marker == null) {
                mapView.setVisibility(View.GONE);
                return;
            }

            final Context context = mapView.getContext();

            map = googleMap;
            map.setMapType(MapHelper.getMapType(context));

            final LatLng position = new LatLng(marker.getLatitude(), marker.getLongitude());
            map.addMarker(new MarkerOptions()
                    .position(position));
            final CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position)
                    .zoom(context.getResources().getInteger(R.integer.simple_map_default_zoom))
                    .build();
            final CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.moveCamera(cameraUpdate);
        }
    }
}
