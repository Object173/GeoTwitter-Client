package com.object173.geotwitter.service.authorization;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public final class AuthenticatorService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        final AuthAccount authenticator = new AuthAccount(this);
        return authenticator.getIBinder();
    }
}
