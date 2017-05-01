package com.object173.geotwitter.util;

import android.app.Activity;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.object173.geotwitter.gui.login.LoginActivity;

/**
 * Created by Object173
 * on 26.04.2017.
 */

public final class LoginManager {

    public static boolean logout(final Activity activity) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        if(activity != null && auth != null && auth.getCurrentUser() != null) {
            auth.signOut();
            activity.startActivity(new Intent(activity, LoginActivity.class));
            activity.finish();
            return true;
        }
        return false;
    }
}
