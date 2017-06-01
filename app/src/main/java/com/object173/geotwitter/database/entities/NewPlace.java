package com.object173.geotwitter.database.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.server.json.NewPlaceJson;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class NewPlace implements Serializable{

    private long id;
    private long authorId;
    private String title;
    private String body;
    private long markerId;
    private Date date;

    private Profile author;
    private Marker marker;
    private List<Image> images;

    public NewPlace() {
    }

    public static NewPlace newInstance(final NewPlaceJson newPlaceJson) {
        if(newPlaceJson == null) {
            return null;
        }
        final NewPlace place = new NewPlace();
        place.setId(newPlaceJson.getId());
        place.setAuthorId(newPlaceJson.getAuthorId());
        place.setTitle(newPlaceJson.getTitle());
        place.setBody(newPlaceJson.getBody());
        place.setMarker(Marker.newInstance(newPlaceJson.getMarker()));
        if(newPlaceJson.getImages() != null && newPlaceJson.getImages().size() > 0) {
            final List<String> urlList = newPlaceJson.getImages();
            final List<Image> imageList = new ArrayList<>();
            for(String url : urlList) {
                imageList.add(Image.newNetworkImage(url));
            }
            place.setImages(imageList);
        }
        place.setDate(newPlaceJson.getDate());
        return place;
    }

    public NewPlace(final Cursor cursor) {
        if(cursor == null) {
            return;
        }

        id = cursor.getLong(0);
        authorId = cursor.getLong(1);
        title = cursor.getString(2);
        body = cursor.getString(3);
        markerId = cursor.getLong(4);
        date = new Date(cursor.getLong(5));
    }

    public ContentValues insert() {
        final ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlaceTableScheme._ID, id);
        values.put(DatabaseContract.PlaceTableScheme.COLUMN_AUTHOR, authorId);
        values.put(DatabaseContract.PlaceTableScheme.COLUMN_TITLE, title);
        values.put(DatabaseContract.PlaceTableScheme.COLUMN_BODY, body);
            values.put(DatabaseContract.PlaceTableScheme.COLUMN_MARKER, markerId);
        if(date != null) {
            values.put(DatabaseContract.PlaceTableScheme.COLUMN_DATE, date.getTime());
        }
        return values;
    }

    public ContentValues insertImage(final Image image) {
        if(image == null) {
            return null;
        }
        return insertImage(id, image.getId());
    }

    public static ContentValues insertImage(final long placeId, final long imageId) {
        final ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlaceImageTableScheme.COLUMN_PLACE, placeId);
        values.put(DatabaseContract.PlaceImageTableScheme.COLUMN_IMAGE, imageId);
        return values;
    }

    public static long getImageId(final Cursor cursor) {
        if(cursor == null) {
            return DatabaseContract.NULL_ID;
        }
        return cursor.getLong(1);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getMarkerId() {
        return markerId;
    }

    public void setMarkerId(long markerId) {
        this.markerId = markerId;
    }

    public Profile getAuthor() {
        return author;
    }

    public void setAuthor(Profile author) {
        this.author = author;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public final String getDateTimeString() {
        if(date == null) {
            return null;
        }
        final DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        return dateFormat.format(date);
    }
}
