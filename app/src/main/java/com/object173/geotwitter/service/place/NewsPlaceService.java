package com.object173.geotwitter.service.place;

import android.content.Context;
import android.content.Intent;

import com.object173.geotwitter.database.entities.Marker;
import com.object173.geotwitter.server.json.Filter;
import com.object173.geotwitter.service.BaseService;

import java.util.ArrayList;

public final class NewsPlaceService {

    private static final int NULL_ID = BaseService.NULL_ID;

    public static long startToAddPlace(final Context context, final String title, final String body,
                                       final Marker marker, final ArrayList<String> images) {
        if(context == null || title == null || body == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = CrudPlaceTask.createAddIntent(context, title, body, marker, images, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }

    public static long startToLoadTopPlace(final Context context, final long authorId) {
        if(context == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = LoadPlaceTask.createLoadTopIntent(context, authorId, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }

    public static long startToLoadBottomPlace(final Context context, final long authorId, final int size,
                                              final int offset) {
        if(context == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = LoadPlaceTask.createLoadBottomIntent(context, authorId, new Filter(size, offset), requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }
}
