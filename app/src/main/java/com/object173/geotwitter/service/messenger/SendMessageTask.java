package com.object173.geotwitter.service.messenger;

import android.content.Context;
import android.content.Intent;

import com.object173.geotwitter.GeoTwitterApp;
import com.object173.geotwitter.database.entities.Dialog;
import com.object173.geotwitter.database.entities.Image;
import com.object173.geotwitter.database.entities.Marker;
import com.object173.geotwitter.database.entities.Message;
import com.object173.geotwitter.database.service.DialogService;
import com.object173.geotwitter.database.service.ImageService;
import com.object173.geotwitter.database.service.MessageService;
import com.object173.geotwitter.server.ServerContract;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.server.json.MessageJson;
import com.object173.geotwitter.service.BaseServiceTask;
import com.object173.geotwitter.util.NetworkConnectionManager;
import com.object173.geotwitter.util.resources.ImagesManager;
import com.object173.geotwitter.util.user.AuthManager;

import java.io.File;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public final class SendMessageTask extends BaseServiceTask {

    private static final String TYPE_NEW_MESSAGE = "send_new_message";
    private static final String TYPE_RESEND = "resend_message";

    private static final String KEY_COMPANION_ID = "companion_id";
    private static final String KEY_TEXT = "text";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_MARKER = "marker";
    private static final String KEY_MESSAGE_ID = "message_id";

    public static boolean isThisIntent(final Intent intent) {
        return isNewMessageIntent(intent) || isResendIntent(intent);
    }

    public static boolean isNewMessageIntent(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_NEW_MESSAGE);
    }

    public static boolean isResendIntent(final Intent intent) {
        return intent != null && isThisIntent(intent, TYPE_RESEND);
    }

    public static Intent createNewMessageIntent(final Context context, final long companionId,
                                                final String text, final String imagePath,
                                                final Marker marker, final long requestId) {
        if(context == null) {
            return null;
        }
        final Intent intent = BaseServiceTask.createBaseInputIntent(context, TYPE_NEW_MESSAGE, requestId);
        intent.putExtra(KEY_COMPANION_ID, companionId);
        intent.putExtra(KEY_IMAGE, imagePath);
        intent.putExtra(KEY_MARKER, marker);
        intent.putExtra(KEY_TEXT, text);
        return intent;
    }

    public static Intent createResendIntent(final Context context, final long messageId, final long requestId) {
        if(context == null) {
            return null;
        }
        final Intent intent = BaseServiceTask.createBaseInputIntent(context, TYPE_RESEND, requestId);
        intent.putExtra(KEY_MESSAGE_ID, messageId);
        return intent;
    }

    public static Intent startTask(final Context context, final Intent intent) {
        if (intent == null || context == null || !AuthManager.isAuth()) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        if(isNewMessageIntent(intent)) {
            return startNewMessageTask(context, intent);
        } else
        if(isResendIntent(intent)) {
            return startResendTask(context, intent);
        }
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }

    public static Intent startNewMessageTask(final Context context, final Intent intent) {
        if (intent == null || context == null || !AuthManager.isAuth()) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }

        final long companionId = intent.getLongExtra(KEY_COMPANION_ID, NULL_ID);
        final String text = intent.getStringExtra(KEY_TEXT);
        final String imagePath = intent.getStringExtra(KEY_IMAGE);
        final Marker marker = (Marker) intent.getSerializableExtra(KEY_MARKER);

        final Dialog dialog = DialogService.getCompanionDialog(context, companionId);
        if(dialog == null) {
            return createResultIntent(intent, AuthResult.Result.FAIL);
        }

        final Message message = new Message(dialog.getId(), AuthManager.getAuthToken().getUserId(),
                text, new Date(), Message.STATUS_WAIT);
        if(marker != null) {
            message.setMarker(marker);
        }

        if(imagePath != null) {
            final long imageId = ImageService.addImage(context, Image.newNetworkImage(""));
            final Image image = ImageService.getImage(context, imageId);
            if(ImagesManager.preparationImage(context, imagePath, image.getLocalPath())) {
                message.setImage(image);
                ImagesManager.invalidateImage(context, image);
            }
        }

        final long localId = MessageService.addUserMessage(context, message);
        if(localId <= NULL_ID) {
            return createResultIntent(intent, AuthResult.Result.FAIL);
        }

        return sendMessage(context, intent, dialog, message, localId);
    }

    public static Intent startResendTask(final Context context, final Intent intent) {
        if (intent == null || context == null || !AuthManager.isAuth()) {
            return createResultIntent(intent, AuthResult.Result.NULL_POINTER);
        }
        final long messageId = intent.getLongExtra(KEY_MESSAGE_ID, NULL_ID);
        final Message message = MessageService.getMessage(context, messageId);

        if(message == null) {
            return createResultIntent(intent, AuthResult.Result.FAIL);
        }

        final Dialog dialog = DialogService.getDialog(context, message.getDialogId());

        if(dialog == null) {
            return createResultIntent(intent, AuthResult.Result.FAIL);
        }

        MessageService.setStatus(context, message.getLocalId(), Message.STATUS_WAIT);
        DialogService.invalidateDialog(context, dialog.getId());

        return sendMessage(context, intent, dialog, message, messageId);
    }

    private static Intent sendMessage(final Context context, final Intent intent, final Dialog dialog,
                                       final Message message, final long localId) {
        if(context == null || dialog == null || message == null) {
            return createResultIntent(intent, AuthResult.Result.FAIL);
        }

        final MessageJson messageJson = MessageJson.newInstance(message);
        messageJson.setSenderId(dialog.getCompanionId());

        if(!NetworkConnectionManager.isConnection(context)) {
            MessageService.setStatus(context, message.getLocalId(), Message.STATUS_FAIL);
            DialogService.invalidateDialog(context, dialog.getId());
            return createResultIntent(intent, AuthResult.Result.NO_INTERNET);
        }

        final MultipartBody.Part avatarPart;
        if(message.getImage() != null) {
            final File image = new File(message.getImage().getLocalPath());
            final RequestBody imageFile = RequestBody.create(MediaType.parse(ServerContract.MEDIA_TYPE_FILE), image);
            avatarPart = MultipartBody.Part.createFormData(ServerContract.ATTR_IMAGE_NAME, image.getName(), imageFile);
        }
        else {
            avatarPart = null;
        }

        try {
            final MessageJson result = GeoTwitterApp.getApi()
                    .sendMessage(AuthManager.getAuthToken(), messageJson, avatarPart)
                    .execute().body();

            final Message newMessage = new Message(result, Message.STATUS_SUCCESS);
            newMessage.setLocalId(localId);
            MessageService.addUserMessage(context, newMessage);
            DialogService.invalidateDialog(context, dialog.getId());
            return createResultIntent(intent, AuthResult.Result.SUCCESS);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        MessageService.setStatus(context, message.getLocalId(), Message.STATUS_FAIL);
        DialogService.invalidateDialog(context, dialog.getId());
        return createResultIntent(intent, AuthResult.Result.FAIL);
    }
}
