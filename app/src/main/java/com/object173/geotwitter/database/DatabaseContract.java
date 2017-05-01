package com.object173.geotwitter.database;

/**
 * Created by Object173
 * on 28.04.2017.
 */

public final class DatabaseContract {
    private final static String REFERENCE_IMAGES = "images";

    private final static String SEPARATOR = "/";

    public static String getReferenceImages(final String filename) {
        if(filename == null) {
            return null;
        }
        return REFERENCE_IMAGES+SEPARATOR+filename;
    }
}
