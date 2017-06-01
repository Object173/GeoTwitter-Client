package com.object173.geotwitter.server.json;

import com.object173.geotwitter.database.entities.Marker;

public final class PlaceFilter {
    private MarkerJson markerJson;
    private String search;
    private double distance;
    private int days;

    public PlaceFilter() {
    }

    public PlaceFilter(Marker marker, String search, int distance, int days) {
        this.markerJson = MarkerJson.newInstance(marker);
        this.search = search;
        this.distance = distance * 1000;
        this.days = days;
    }

    public MarkerJson getMarkerJson() {
        return markerJson;
    }

    public void setMarkerJson(MarkerJson markerJson) {
        this.markerJson = markerJson;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
