package com.object173.geotwitter.database.service;


import android.content.Context;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.DatabaseHelper;
import com.object173.geotwitter.database.entities.Marker;
import com.object173.geotwitter.database.repository.MarkerRepository;

public final class MarkerService {

    private static final long NULL_ID = DatabaseContract.NULL_ID;

    public static Marker getMarker(final Context context, final long id) {
        if(context == null || id <= NULL_ID) {
            return null;
        }
        try {
            final DatabaseHelper db = new DatabaseHelper(context);
            final Marker result = MarkerRepository.findById(db.getReadableDatabase(), id);
            db.close();
            return result;
        }
        catch (Exception ex) {
            return null;
        }
    }
}
