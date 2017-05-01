package com.object173.geotwitter;

import android.app.Application;

import com.object173.geotwitter.server.ServerApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Object173
 * on 30.04.2017.
 */

public final class GeoTwitterApp extends Application {

    private static ServerApi serverApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder()
                .baseUrl(ServerApi.HOST) //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        serverApi = retrofit.create(ServerApi.class); //Создаем объект, при помощи которого будем выполнять запросы
    }

    public static ServerApi getApi() {
        return serverApi;
    }
}
