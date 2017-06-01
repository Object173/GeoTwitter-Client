package com.object173.geotwitter.service.messenger;


import android.content.Context;
import android.content.Intent;

import com.object173.geotwitter.database.entities.Marker;
import com.object173.geotwitter.server.json.Filter;
import com.object173.geotwitter.service.BaseService;

public final class MessengerService {

    private static final int NULL_ID = BaseService.NULL_ID;

    public static long startToGetDialog(final Context context, final long companionId) {
        if(context == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = LoadDialogsTask.createGetIntent(context, companionId, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }

    public static long startToUpdateDialogs(final Context context) {
        if(context == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = LoadDialogsTask.createUpdateIntent(context, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }

    public static long startToGetLastMessages(final Context context, final long dialogId) {
        if(context == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = LoadMessagesTask.createGetLastIntent(context, dialogId, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }

    public static long startToLoadListMessages(final Context context, final long dialogId,
                                               final int blockSize, final int offset) {
        if(context == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = LoadMessagesTask.createLoadListIntent(context, dialogId,
                new Filter(blockSize, offset), requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }

    public static long startToSendTextMessage(final Context context, final long companionId, final String text) {
        if(text == null || text.length() <= 0) {
            return NULL_ID;
        }
        return startToSendNewMessage(context, companionId, text, null, null);
    }

    public static long startToSendImageMessage(final Context context, final long companionId, final String imagePath) {
        if(imagePath == null) {
            return NULL_ID;
        }
        return startToSendNewMessage(context, companionId, null, imagePath, null);
    }

    public static long startToSendMarkerMessage(final Context context, final long companionId, final Marker marker) {
        if(marker == null) {
            return NULL_ID;
        }
        return startToSendNewMessage(context, companionId, null, null, marker);
    }

    private static long startToSendNewMessage(final Context context, final long companionId,
                                             final String text, final String imagePath, final Marker marker) {
        if(context == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = SendMessageTask.createNewMessageIntent(context, companionId, text,
                imagePath, marker, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }

    public static long startToResendMessage(final Context context, final long messageId) {
        if(context == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = SendMessageTask.createResendIntent(context, messageId, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }
}
