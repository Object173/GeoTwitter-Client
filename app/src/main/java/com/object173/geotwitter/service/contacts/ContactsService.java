package com.object173.geotwitter.service.contacts;

import android.content.Context;
import android.content.Intent;

import com.object173.geotwitter.service.BaseService;

public final class ContactsService {

    private static final int NULL_ID = BaseService.NULL_ID;

    public static long startToSendInvite(final Context context, final long contactId) {
        if(context == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = InviteTask.createAddInviteIntent(context, contactId, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }

    public static long startToRemoveInvite(final Context context, final long contactId) {
        if(context == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = InviteTask.createRemoveInviteIntent(context, contactId, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }

    public static long startToUpdateContacts(final Context context) {
        if(context == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = UpdateContactsTask.createUpdateIntent(context, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }
}
