package com.object173.geotwitter;

import android.app.Application;

import com.object173.geotwitter.server.ServerApi;
import com.object173.geotwitter.util.resources.CacheManager;
import com.object173.geotwitter.util.user.AuthManager;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Object173
 * on 30.04.2017.
 */

public final class GeoTwitterApp extends Application {

    private static ServerApi serverApi;

    @Override
    public void onCreate() {
        super.onCreate();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApi.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serverApi = retrofit.create(ServerApi.class);

        CacheManager.onCreate(getBaseContext());
        AuthManager.readCurrentUser(getBaseContext());
    }

    public static ServerApi getApi() {
        return serverApi;
    }
}
