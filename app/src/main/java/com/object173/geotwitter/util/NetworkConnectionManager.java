package com.object173.geotwitter.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Object173
 * on 13.04.2017.
 */

public final class NetworkConnectionManager {

    public static boolean isConnection(final Context context) {
        if(context == null) {
            return false;
        }

        final ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connMgr != null) {
            final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return  networkInfo != null && networkInfo.isConnected();
        }
        else {
            return false;
        }
    }
}
