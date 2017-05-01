package com.object173.geotwitter.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Object173
 * on 10.04.2017.
 */

public abstract class BaseService extends Service {
    public static final String ACTION = "com.object173.geotwitter.service";

    private final ExecutorService poolThread;
    private final List<Long> currentTaskList = new ArrayList<>();

    private final IBinder mBinder = new MainBinder();

    public static final int NULL_ID = -1;

    public BaseService(final int pool_size) {
        poolThread = Executors.newFixedThreadPool(pool_size);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {
        if(intent != null) {
            Log.d("AuthService","onStartCommand");
            poolThread.execute(new ServiceTask(intent));
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        poolThread.shutdown();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    protected static long createRequestId() {
        return new Date().getTime();
    }

    protected static long startToIntent(final Context context, final Intent intent, final long requestId) {
        try {
            context.startService(intent);
            return requestId;
        }
        catch (Exception ex) {
            return NULL_ID;
        }
    }

    protected abstract boolean onHandleIntent(final Intent intent, final ServiceTask task);

    public class MainBinder extends Binder {
        public boolean isTaskRunning(final long requestId) {
            return BaseService.this.isTaskRunning(requestId);
        }
    }

    private void addCurrentTask(final long requestId) {
        currentTaskList.add(requestId);
    }

    private void finishTask(final long requestId) {
        currentTaskList.remove(requestId);
        if(currentTaskList.size() == 0) {
            stopSelf();
        }
    }

    private boolean isTaskRunning(final long requestId) {
        return (currentTaskList.indexOf(requestId) >= 0);
    }

    protected class ServiceTask implements Runnable, BaseServiceTask.OnFinishTaskListener{
        private final Intent intent;
        private final long requestId;
        private boolean isAlive = true;

        ServiceTask(final Intent intent) {
            this.requestId = BaseServiceTask.getRequestId(intent);
            this.intent = intent;
        }

        @Override
        public void run() {
            Log.d("AuthService","requestId "+requestId);
            if(requestId <= NULL_ID) {
                return;
            }
            addCurrentTask(requestId);
            isAlive = onHandleIntent(intent, this);
            while (isAlive) {
            }
        }

        @Override
        public void finishTask(final Intent intent) {
            isAlive = false;
            if (intent != null) {
                sendBroadcast(intent);
            }
            BaseService.this.finishTask(requestId);
        }
    }
}
