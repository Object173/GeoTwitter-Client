package com.object173.geotwitter.gui.messenger;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.object173.geotwitter.R;
import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.entities.Dialog;
import com.object173.geotwitter.database.entities.Marker;
import com.object173.geotwitter.database.service.DialogService;
import com.object173.geotwitter.database.service.MessageService;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.gui.util.RefreshRecyclerListener;
import com.object173.geotwitter.gui.util.ServiceConnectionController;
import com.object173.geotwitter.server.json.AuthProfile;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.service.messenger.MessengerService;
import com.object173.geotwitter.service.messenger.SendMessageTask;
import com.object173.geotwitter.util.resources.ChooserManager;

public class DialogActivity extends MyBaseActivity
        implements ServiceConnectionController.ServiceConnector{

    private Dialog dialog;
    private ProfileToolbar profileToolbar;

    private EditText messageField;

    private static final String ARG_DIALOG_ID = "dialog_id";

    private BottomSheetBehavior bottomSheetBehavior = null;
    private int bottomSheetState;
    private static final String KEY_BOTTOM_SHEET_STATE = "bottom_sheet_state";

    private final ServiceConnectionController connectionController = new ServiceConnectionController();

    private final RefreshRecyclerListener refreshRecyclerListener = new RefreshRecyclerListener() {
        @Override
        public void onLoadMore(String request, int size, int offset) {
            if(getBaseContext() != null && dialog != null) {
                MessengerService.startToLoadListMessages(getBaseContext(), dialog.getId(), size, offset);
            }
        }
    };

    public static Intent getStartIntent(final Context context, final long dialogId) {
        if(context == null || dialogId <= DatabaseContract.NULL_ID) {
            return null;
        }
        final Intent intent = new Intent(context, DialogActivity.class);
        intent.putExtra(ARG_DIALOG_ID, dialogId);
        return intent;
    }

    public static void startActivity(final Context context, final long dialogId) {
        final Intent intent = getStartIntent(context, dialogId);
        if(intent != null) {
            context.startActivity(intent);
        }
    }

    private void initDialog() {
        final long dialogId = getIntent().getExtras().getLong(ARG_DIALOG_ID);
        dialog = DialogService.getDialog(this, dialogId);

        if(dialog == null || dialog.getCompanion() == null) {
            finish();
            return;
        }

        messageField = (EditText) findViewById(R.id.message_field);
        final ImageView sendButton = (ImageView) findViewById(R.id.send_button);
        final ImageView attachmentButton = (ImageView) findViewById(R.id.attachment_button);

        initBottomSheet();

        if(!dialog.getCompanion().getRelationStatus().equals(AuthProfile.RelationStatus.CONTACT)) {
            messageField.setText(R.string.dialog_activity_message_non_contact);
            messageField.setEnabled(false);
            sendButton.setEnabled(false);
            sendButton.setClickable(false);
            attachmentButton.setEnabled(false);
            attachmentButton.setClickable(false);
        }
        else {
            attachmentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    else {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            });
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String message = messageField.getText().toString();
                    sendMessage(message, null, null);
                }
            });
        }
    }

    private void initBottomSheet() {
        final LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(bottomSheetState);

        final Button photoButton = (Button) findViewById(R.id.photo_button);
        final Button imageButton = (Button) findViewById(R.id.image_button);
        final Button markerButton = (Button) findViewById(R.id.marker_button);

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooserManager.showCameraChooser(DialogActivity.this);
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooserManager.showGalleryChooser(DialogActivity.this,
                        getString(R.string.dialog_activity_title_choose_image));
            }
        });
        markerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooserManager.showMarkerChooser(DialogActivity.this);
            }
        });
    }

    private void sendMessage(final String text, final String imagePath, final Marker marker) {
        Log.d("sendMessage","companion " + dialog.getCompanionId());
        if(imagePath != null) {
            MessengerService.startToSendImageMessage(this, dialog.getCompanionId(), imagePath);
        } else
        if(marker != null) {
            MessengerService.startToSendMarkerMessage(this, dialog.getCompanionId(), marker);
        }
        else {
            MessengerService.startToSendTextMessage(getBaseContext(), dialog.getCompanionId(), text);
            messageField.setText("");
        }
        refreshRecyclerListener.setPosition(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!super.onCreate(savedInstanceState, R.layout.activity_dialog, "", true)) {
            finish();
            return;
        }

        if(savedInstanceState != null) {
            bottomSheetState = savedInstanceState.getInt(KEY_BOTTOM_SHEET_STATE, BottomSheetBehavior.STATE_COLLAPSED);
        }
        else {
            bottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;
        }

        initDialog();

        profileToolbar = new ProfileToolbar(dialog.getCompanionId());
        profileToolbar.onCreate(this, savedInstanceState, getToolbar());

        connectionController.onCreate(savedInstanceState);

        refreshRecyclerListener.onCreate(savedInstanceState);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final MessagesAdapter adapter = new MessagesAdapter(MessageService.getMessageList(this, dialog.getId()));
        recyclerView.setAdapter(adapter);
        refreshRecyclerListener.setRecyclerView(this, recyclerView, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectionController.onResume(this, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(profileToolbar != null) {
            profileToolbar.onSaveInstanceState(outState);
        }
        if(bottomSheetBehavior != null) {
            outState.putInt(KEY_BOTTOM_SHEET_STATE, bottomSheetBehavior.getState());
        }
        connectionController.onSaveInstanceState(outState);
        refreshRecyclerListener.saveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        connectionController.onPause(this);
    }

    @Override
    public void receiveMessage(Intent intent) {
        if(SendMessageTask.isThisIntent(intent)) {
            final AuthResult.Result result = SendMessageTask.getAuthResult(intent);
            showReceiveResult(result);
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if(requestCode == ChooserManager.INTENT_PICK_IMAGE) {
            if(resultCode == RESULT_OK) {
                final Uri uri = ChooserManager.getImageUri(this, data);
                sendMessage(null, uri.getPath(), null);
            }
        } else
        if (requestCode == ChooserManager.MARKER_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                final Marker marker = ChooserManager.getMarker(this, data);
                if(marker != null) {
                    MessengerService.startToSendMarkerMessage(this, dialog.getCompanionId(), marker);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finishTask(Intent intent) {
    }

    @Override
    public void finishTask(Class serviceClass) {
    }
}
