package com.object173.geotwitter.gui.contacts;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.object173.geotwitter.GeoTwitterApp;
import com.object173.geotwitter.server.json.AuthProfile;
import com.object173.geotwitter.server.json.Filter;
import com.object173.geotwitter.util.user.AuthManager;

import java.util.List;

public final class ContactsLoader extends AsyncTaskLoader<List<AuthProfile>> {

    private int size;
    private int offset;
    private String request;

    private static final String ARG_SIZE = "size";
    private static final String ARG_OFFSET = "offset";
    private static final String ARG_REQUEST = "request";

    public static Bundle newInstance(final String request, final int size, final int offset) {
        if(size <= 0 || offset < 0) {
            return null;
        }
        final Bundle bundle = new Bundle();
        bundle.putInt(ARG_SIZE, size);
        bundle.putInt(ARG_OFFSET, offset);
        bundle.putString(ARG_REQUEST, request);
        return bundle;
    }

    public ContactsLoader(final Context context, final Bundle args) {
        super(context);
        size = args.getInt(ARG_SIZE);
        offset = args.getInt(ARG_OFFSET);
        request = args.getString(ARG_REQUEST);
    }

    @Override
    public List<AuthProfile> loadInBackground() {
        Log.d("AddContactActivity","loadInBackground "+ request + " " + offset);
        try {
            final Filter filter = new Filter(request, size, offset);
            final List<AuthProfile> result =
                    GeoTwitterApp.getApi().getAllContacts(AuthManager.getAuthToken(), filter).execute().body();
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
