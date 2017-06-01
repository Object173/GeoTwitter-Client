package com.object173.geotwitter.server.json;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewPlaceJson {

    private long id;
    private long authorId;
    private String title;
    private String body;
    private MarkerJson marker;

    private Date date;

    private List<String> images;

    public NewPlaceJson() {
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

    public MarkerJson getMarker() {
        return marker;
    }

    public void setMarker(MarkerJson marker) {
        this.marker = marker;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
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
