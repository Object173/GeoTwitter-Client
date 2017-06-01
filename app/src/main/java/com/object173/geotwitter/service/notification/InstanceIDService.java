package com.object173.geotwitter.service.notification;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.object173.geotwitter.GeoTwitterApp;
import com.object173.geotwitter.util.PreferencesManager;
import com.object173.geotwitter.util.user.AuthManager;

import java.io.IOException;

public final class InstanceIDService extends FirebaseInstanceIdService {

    private static final String KEY_FCM_TOKEN = "fcm_token";

    @Override
    public void onTokenRefresh() {
        final String token = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(final String token) {
        if(token != null) {
            PreferencesManager.setStringPreference(getBaseContext(), KEY_FCM_TOKEN, token);
            if(AuthManager.isAuth()) {
                try {
                    GeoTwitterApp.getApi().refreshFcmToken(AuthManager.getAuthToken(), token).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getToken(final Context context) {
        if(context == null) {
            return null;
        }
        return PreferencesManager.getStringPreference(context, KEY_FCM_TOKEN);
    }

    public static void signOut() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Log.d("signOut","start");
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                    FirebaseInstanceId.getInstance().getToken();
                } catch (IOException e) {
                    Log.d("signOut", e.toString());
                }
                return null;
            }
        }.execute();
    }
}
