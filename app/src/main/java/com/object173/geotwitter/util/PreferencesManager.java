package com.object173.geotwitter.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.CameraPosition;
import com.object173.geotwitter.R;
import com.object173.geotwitter.gui.util.CameraState;

public final class PreferencesManager {

    private PreferencesManager() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static String getStringPreference(final Context context, final String key) {
        if(context == null || key == null) {
            return null;
        }
        try {
            final SharedPreferences optionsPreference = PreferenceManager.getDefaultSharedPreferences(context);
            return optionsPreference.getString(key, null);
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static void setStringPreference(final Context context, final String key, final String value) {
        if(context == null || key == null) {
            return;
        }
        final SharedPreferences optionsPreference = PreferenceManager.getDefaultSharedPreferences(context);
        optionsPreference.edit().putString(key, value).apply();
    }

    public static long getLongPreference(final Context context, final String key, final long defaultValue) {
        if(context == null || key == null) {
            return defaultValue;
        }
        try {
            final SharedPreferences optionsPreference = PreferenceManager.getDefaultSharedPreferences(context);
            return optionsPreference.getLong(key, defaultValue);
        }
        catch (Exception ex) {
            return defaultValue;
        }
    }

    public static int getIntPreference(final Context context, final String key, final int defaultValue) {
        if(context == null || key == null) {
            return defaultValue;
        }
        try {
            final SharedPreferences optionsPreference = PreferenceManager.getDefaultSharedPreferences(context);
            return optionsPreference.getInt(key, defaultValue);
        }
        catch (Exception ex) {
            return defaultValue;
        }
    }

    public static void setIntPreference(final Context context, final String key, final int value) {
        if(context == null || key == null) {
            return;
        }
        final SharedPreferences optionsPreference = PreferenceManager.getDefaultSharedPreferences(context);
        optionsPreference.edit().putInt(key, value).apply();
    }

    public static boolean getBooleanPreference(final Context context, final String key, final boolean defaultValue) {
        if(context == null || key == null) {
            return defaultValue;
        }
        try {
            final SharedPreferences optionsPreference = PreferenceManager.getDefaultSharedPreferences(context);
            return optionsPreference.getBoolean(key, defaultValue);
        }
        catch (Exception ex) {
            return defaultValue;
        }
    }

    public static void setBooleanPreference(final Context context, final String key, final boolean value) {
        if(context == null || key == null) {
            return;
        }
        final SharedPreferences optionsPreference = PreferenceManager.getDefaultSharedPreferences(context);
        optionsPreference.edit().putBoolean(key, value).apply();
    }

    public static boolean isMapSputnik(final Context context) {
        return context != null &&
                getBooleanPreference(context, context.getString(R.string.key_preference_enabled_sputnik),
                        context.getResources().getBoolean(R.bool.preference_enabled_sputnik_default));
    }

    public static void setCameraPosition(final Context context, final CameraPosition position) {
        if(context == null || position == null) {
            return;
        }
        try {
            final SharedPreferences.Editor editor =
                    PreferenceManager.getDefaultSharedPreferences(context).edit();
            final CameraState cameraState = new CameraState(position);
            cameraState.saveState(editor);
            editor.apply();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static CameraPosition getCameraPosition(final Context context) {
        if(context == null) {
            return null;
        }
        try {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            final CameraState cameraState = new CameraState(preferences);
            return cameraState.getCameraPosition();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
