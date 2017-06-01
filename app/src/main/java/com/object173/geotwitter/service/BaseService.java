package com.object173.geotwitter.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.object173.geotwitter.service.authorization.EditProfileTask;
import com.object173.geotwitter.service.authorization.RegisterTask;
import com.object173.geotwitter.service.authorization.SignInTask;
import com.object173.geotwitter.service.contacts.InviteTask;
import com.object173.geotwitter.service.contacts.UpdateContactsTask;
import com.object173.geotwitter.service.messenger.LoadDialogsTask;
import com.object173.geotwitter.service.messenger.LoadMessagesTask;
import com.object173.geotwitter.service.messenger.SendMessageTask;
import com.object173.geotwitter.service.place.CrudPlaceTask;
import com.object173.geotwitter.service.place.LoadPlaceTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class BaseService extends Service {
    public static final String ACTION = "com.object173.geotwitter.service";

    private final ExecutorService poolThread = Executors.newCachedThreadPool();
    private final List<Long> currentTaskList = new ArrayList<>();

    private final IBinder mBinder = new MainBinder();

    public static final int NULL_ID = 0;

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {
        if(intent != null) {
            Log.d("AuthService","onStartCommand");
            final ServiceTask task = new ServiceTask(intent);
            addCurrentTask(task.getRequestId());
            poolThread.submit(task);
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("AuthService","onDestroy");
        poolThread.shutdown();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static long createRequestId() {
        return new Date().getTime();
    }

    public static long startToIntent(final Context context, final Intent intent, final long requestId) {
        try {
            context.startService(intent);
            return requestId;
        }
        catch (Exception ex) {
            return NULL_ID;
        }
    }

    private Intent onHandleIntent(final Intent intent) {
        if(intent == null) {
            return null;
        }
        if(SignInTask.isThisIntent(intent)) {
            return SignInTask.startTask(getBaseContext(), intent);
        } else
        if(RegisterTask.isThisIntent(intent)) {
            return RegisterTask.startTask(getBaseContext(), intent);
        } else
        if(EditProfileTask.isThisIntent(intent)) {
            return EditProfileTask.startTask(getBaseContext(), intent);
        } else
        if(InviteTask.isThisIntent(intent)) {
            return InviteTask.startTask(getBaseContext(), intent);
        } else
        if(UpdateContactsTask.isThisIntent(intent)) {
            return UpdateContactsTask.startTask(getBaseContext(), intent);
        } else
        if(LoadDialogsTask.isThisIntent(intent)) {
            return LoadDialogsTask.startTask(getBaseContext(), intent);
        } else
        if(LoadMessagesTask.isThisIntent(intent)) {
            return LoadMessagesTask.startTask(getBaseContext(), intent);
        } else
        if(SendMessageTask.isThisIntent(intent)) {
            return SendMessageTask.startTask(getBaseContext(), intent);
        } else
        if(CrudPlaceTask.isThisIntent(intent)) {
            return CrudPlaceTask.startTask(getBaseContext(), intent);
        } else
        if(LoadPlaceTask.isThisIntent(intent)) {
            return LoadPlaceTask.startTask(getBaseContext(), intent);
        }
        return null;
    }

    public class MainBinder extends Binder {
        public boolean isTaskRunning(final long requestId) {
            return BaseService.this.isTaskRunning(requestId);
        }
    }

    private void addCurrentTask(final long requestId) {
        Log.d("AuthService","add task "+requestId);
        currentTaskList.add(requestId);
    }

    private void finishTask(final long requestId) {
        currentTaskList.remove(requestId);
        Log.d("AuthService","finishTask size = "+currentTaskList.size());
        if(currentTaskList.size() <= 0) {
            stopSelf();
        }
    }

    private boolean isTaskRunning(final long requestId) {
        return (currentTaskList.indexOf(requestId) >= 0);
    }

    protected class ServiceTask implements Runnable {
        private final Intent intent;
        private final long requestId;
        ServiceTask(final Intent intent) {
            this.requestId = BaseServiceTask.getRequestId(intent);
            this.intent = intent;
        }
        @Override
        public void run() {
            if(requestId <= NULL_ID) {
                return;
            }
            final Intent result = onHandleIntent(intent);
            if (result != null) {
                sendBroadcast(result);
            }
            BaseService.this.finishTask(requestId);
        }
        long getRequestId() {
            return requestId;
        }
    }
}
