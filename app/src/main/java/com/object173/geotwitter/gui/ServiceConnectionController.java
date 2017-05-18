package com.object173.geotwitter.gui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.object173.geotwitter.service.BaseService;
import com.object173.geotwitter.service.BaseServiceTask;

/**
 * Created by Object173
 * on 14.04.2017.
 */

public final class ServiceConnectionController {

    private BaseService.MainBinder binder = null;
    private final Class serviceClass = BaseService.class;
    private final String filterAction = BaseService.ACTION;

    private static final String KEY_REQUEST_ID = "request_id";
    private long requestId = BaseService.NULL_ID;

    private ServiceConnector connector = null;

    public interface ServiceConnector {
        void receiveMessage(Intent intent);
        void finishTask(Intent intent);
        void finishTask(Class serviceClass);
    }

    public final void onCreate(final Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            requestId = savedInstanceState.getLong(KEY_REQUEST_ID, BaseService.NULL_ID);
        }
    }

    public final void onResume(final Context context, final ServiceConnector connector) {
        if(context == null) {
            return;
        }
        this.connector = connector;

        final IntentFilter filter = new IntentFilter(filterAction);
        context.registerReceiver(broadcastReceiver, filter);

        if(requestId > BaseService.NULL_ID) {
            final Intent intent = new Intent(context, serviceClass);
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public final void onSaveInstanceState(final Bundle outState) {
        outState.putLong(KEY_REQUEST_ID, requestId);
    }

    public final void onPause(final Context context) {
        if(context == null) {
            return;
        }

        context.unregisterReceiver(broadcastReceiver);
        unbind(context);
        connector = null;
    }

    private void unbind(final Context context) {
        if(context != null && binder != null) {
            context.unbindService(serviceConnection);
            binder = null;
        }
    }

    public boolean setRequestId(final long requestId) {
        if(requestId <= BaseService.NULL_ID) {
            return false;
        }
        this.requestId = requestId;
        return true;
    }

    public boolean isTaskRunning() {
        return requestId > BaseService.NULL_ID;
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if(intent == null) {
                return;
            }
            if(connector != null) {
                if(BaseServiceTask.getRequestId(intent) == requestId) {
                    requestId = BaseService.NULL_ID;
                    connector.finishTask(intent);
                }
                else {
                    connector.receiveMessage(intent);
                }
            }
        }
    };

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(final ComponentName className,
                                       final IBinder service) {
            binder = (BaseService.MainBinder) service;
            if(requestId > BaseService.NULL_ID && !binder.isTaskRunning(requestId)) {
                requestId = BaseService.NULL_ID;
                if(connector != null) {
                    connector.finishTask(serviceClass);
                }
            }
        }

        @Override
        public void onServiceDisconnected(final ComponentName arg0) {
            binder = null;
        }
    };
}
