package com.object173.geotwitter.database.service;

import android.content.Context;
import android.database.Cursor;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.DatabaseHelper;
import com.object173.geotwitter.database.MyContentProvider;
import com.object173.geotwitter.database.entities.NewPlace;
import com.object173.geotwitter.database.repository.MarkerRepository;
import com.object173.geotwitter.database.repository.PlaceRepository;
import com.object173.geotwitter.server.json.NewPlaceJson;
import com.object173.geotwitter.util.user.AuthManager;

import java.util.ArrayList;
import java.util.List;

public final class PlaceService {

    public static NewPlace getPlace(final Context context, final long id) {
        if(context == null) {
            return null;
        }
        try {
            final DatabaseHelper dbHelper = new DatabaseHelper(context);
            final NewPlace place = PlaceRepository.findById(dbHelper.getReadableDatabase(), id);
            dbHelper.close();
            return place;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static NewPlace getPlace(final Context context, final Cursor cursor) {
        if(context == null || cursor == null) {
            return null;
        }
        try {
            final DatabaseHelper db = new DatabaseHelper(context);

            final NewPlace place = new NewPlace(cursor);
            place.setAuthor(ProfileService.getProfile(context, place.getAuthorId()));
            place.setMarker(MarkerService.getMarker(context, place.getMarkerId()));
            place.setImages(PlaceRepository.getPlaceImages(db.getReadableDatabase(), place.getId()));

            db.close();
            return place;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static boolean addNewPlace(final Context context, final NewPlaceJson newPlaceJson) {
        final List<NewPlaceJson> placeList = new ArrayList<>();
        placeList.add(newPlaceJson);
        return addNewPlace(context, placeList);
    }

    public static boolean addNewPlace(final Context context, final List<NewPlaceJson> placeJsonList) {
        if(context == null || placeJsonList == null) {
            return false;
        }
        try {
            final DatabaseHelper db = new DatabaseHelper(context);

            for(final NewPlaceJson placeJson: placeJsonList) {
                final NewPlace newPlace = NewPlace.newInstance(placeJson);

                if(PlaceRepository.findById(db.getReadableDatabase(), newPlace.getId()) != null) {
                    continue;
                }

                if(newPlace.getMarker() != null) {
                    final long markerId = MarkerRepository.insert(db.getWritableDatabase(), newPlace.getMarker());
                    newPlace.setMarkerId(markerId);
                }

                context.getContentResolver().insert(MyContentProvider.PLACES_CONTENT_URI, newPlace.insert());

                if(newPlace.getImages() != null) {
                    PlaceRepository.insertImageList(db.getWritableDatabase(), newPlace.getImages(), newPlace.getId());
                }
            }

            db.close();
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean cropPlaceList(final Context context, final int listSize) {
        if(context == null || !AuthManager.isAuth()) {
            return false;
        }
        try {
            context.getContentResolver().delete(MyContentProvider.PLACES_CONTENT_URI,
                    DatabaseContract.PlaceTableScheme.COLUMN_AUTHOR + " <> ? AND " +
                            DatabaseContract.PlaceTableScheme._ID + " NOT IN (" +
                            "SELECT " + DatabaseContract.PlaceTableScheme._ID + " FROM " +
                            DatabaseContract.PlaceTableScheme.TABLE_NAME + " where " +
                            DatabaseContract.PlaceTableScheme.COLUMN_AUTHOR + " <> ? " +
                            " ORDER BY " + DatabaseContract.PlaceTableScheme.COLUMN_DATE + " DESC LIMIT ?)",
                    new String[] {String.valueOf(AuthManager.getAuthToken().getUserId()),
                            String.valueOf(AuthManager.getAuthToken().getUserId()),
                            String.valueOf(listSize)});

            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean removeByAuthor(final Context context, final long authorId) {
        if(context == null) {
            return false;
        }
        try {
            context.getContentResolver().delete(MyContentProvider.PLACES_CONTENT_URI,
                    DatabaseContract.PlaceTableScheme.COLUMN_AUTHOR + " = ?",
                    new String[] {String.valueOf(authorId)});

            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static NewPlace getLastPlace(final Context context, final long authorId) {
        if(context == null) {
            return null;
        }
        try {
            final DatabaseHelper dbHelper = new DatabaseHelper(context);
            final NewPlace place = PlaceRepository.findLastByAuthor(dbHelper.getReadableDatabase(), authorId);
            dbHelper.close();
            return place;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static NewPlace getLastPlace(final Context context) {
        if(context == null) {
            return null;
        }
        try {
            final DatabaseHelper dbHelper = new DatabaseHelper(context);
            final NewPlace place = PlaceRepository.findLast(dbHelper.getReadableDatabase());
            dbHelper.close();
            return place;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static Cursor getPlaceList(final Context context, final long authorId) {
        if(context == null) {
            return null;
        }
        if(authorId <= DatabaseContract.NULL_ID) {
            return getPlaceList(context);
        }
        return context.getContentResolver().query(MyContentProvider.PLACES_CONTENT_URI, null,
                DatabaseContract.PlaceTableScheme.COLUMN_AUTHOR + " = ?",
                new String[] {String.valueOf(authorId)},
                DatabaseContract.PlaceTableScheme.COLUMN_DATE + " DESC");
    }

    public static Cursor getPlaceList(final Context context) {
        if(context == null || !AuthManager.isAuth()) {
            return null;
        }

        return context.getContentResolver().query(MyContentProvider.PLACES_CONTENT_URI, null,
                DatabaseContract.PlaceTableScheme.COLUMN_AUTHOR + " <> ?",
                new String[] {String.valueOf(AuthManager.getAuthToken().getUserId())},
                DatabaseContract.PlaceTableScheme.COLUMN_DATE + " DESC");
    }
}
