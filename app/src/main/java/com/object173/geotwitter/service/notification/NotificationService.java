package com.object173.geotwitter.service.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.Profile;
import com.object173.geotwitter.database.service.DialogService;
import com.object173.geotwitter.database.service.ProfileService;
import com.object173.geotwitter.gui.main.MainActivity;
import com.object173.geotwitter.gui.messenger.DialogActivity;
import com.object173.geotwitter.gui.profile.ProfileActivity;
import com.object173.geotwitter.server.ServerContract;
import com.object173.geotwitter.server.json.AuthProfile;
import com.object173.geotwitter.server.json.DialogJson;
import com.object173.geotwitter.server.json.MessageJson;
import com.object173.geotwitter.service.messenger.MessengerService;
import com.object173.geotwitter.util.PreferencesManager;
import com.object173.geotwitter.util.user.AuthManager;

public final class NotificationService extends FirebaseMessagingService {

    private static final int NOTIFICATION_INVITE_ID = 11;
    private static final int NOTIFICATION_MESSAGE_ID = 13;

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        Log.d("NotificationService", "onMessageReceived");
        if(remoteMessage == null) {
            return;
        }
        if(AuthManager.isAuth() && remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {
            final String type = remoteMessage.getData().get(ServerContract.NOTIFICATION_DATA_TYPE);
            final String object = remoteMessage.getData().get(ServerContract.NOTIFICATION_DATA_OBJECT);

            if(type == null || object == null) {
                return;
            }
            switch (type) {
                case ServerContract.NOTIFICATION_TYPE_INVITE: {
                    //преобразуем JSON объект в объект Java
                    final AuthProfile profile = new Gson().fromJson(object, AuthProfile.class);
                    ProfileService.addProfile(getBaseContext(), profile); //добавляем пользователя в БД
                    //если пользователь добавился как подписчик
                    if (profile.getUserId() != AuthManager.getAuthProfile().getUserId() &&
                            profile.getRelation().equals(AuthProfile.RelationStatus.SUBSCRIBER)) {
                        //выводим push уведомление
                        sendNotification(NOTIFICATION_INVITE_ID, getString(R.string.notifications_title_invite),
                                profile.getUsername() + " " + getString(R.string.notifications_body_invite),
                                //создаем намерение для запуска активности профиля
                                ProfileActivity.getStartIntent(getBaseContext(), profile.getUserId()));
                    }
                    break;
                }
                case ServerContract.NOTIFICATION_TYPE_CHANGE_USER: {
                    final AuthProfile profile = new Gson().fromJson(object, AuthProfile.class);
                    ProfileService.updateProfile(getBaseContext(), profile);
                    break;
                }
                case ServerContract.NOTIFICATION_TYPE_CHANGE_AVATAR:
                    final AuthProfile authProfile = new Gson().fromJson(object, AuthProfile.class);
                    ProfileService.updateAvatar(getBaseContext(), authProfile);
                    break;
                case ServerContract.NOTIFICATION_TYPE_NEW_DIALOG:
                    final DialogJson dialog = new Gson().fromJson(object, DialogJson.class);
                    DialogService.addDialog(getBaseContext(), dialog);
                    break;
                case ServerContract.NOTIFICATION_TYPE_NEW_MESSAGE:
                    final MessageJson messageJson = new Gson().fromJson(object, MessageJson.class);

                    if (messageJson != null) {
                        MessengerService.startToGetLastMessages(getBaseContext(), messageJson.getDialogId());
                        final Profile profile = ProfileService.getProfile(getBaseContext(), messageJson.getSenderId());
                        if (profile == null) {
                            return;
                        }

                        String text = messageJson.getText();
                        if (messageJson.getImageUrl() != null) {
                            text = getString(R.string.dialog_activity_message_image);
                        } else if (messageJson.getMarker() != null) {
                            text = getString(R.string.dialog_activity_message_marker);
                        }
                        sendNotification(NOTIFICATION_MESSAGE_ID, profile.getUsername(), text,
                                DialogActivity.getStartIntent(getBaseContext(), messageJson.getDialogId()));
                    }
                    break;
            }
        }
    }

    public void sendNotification(final int id, final String title, final String body, final Intent intent) {
        if(title == null || body == null) {
            return;
        }
        if(!PreferencesManager.getBooleanPreference(getBaseContext(), getString(R.string.key_preference_enabled_notifications),
                getResources().getBoolean(R.bool.preference_enabled_notifications_default))) {
            return;
        }

        final android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getBaseContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true);

        if(PreferencesManager.getBooleanPreference(getBaseContext(), getString(R.string.key_preference_enabled_notifications_sound),
                getResources().getBoolean(R.bool.preference_enabled_notifications_sound_default))) {
            final String soundUrl = PreferencesManager.getStringPreference(getBaseContext(),
                    getString(R.string.key_preference_sound_notifications));
            if (soundUrl != null) {
                mBuilder.setSound(Uri.parse(soundUrl));
            }
            else {
                mBuilder.setDefaults(Notification.DEFAULT_SOUND);
            }
        }
        if(PreferencesManager.getBooleanPreference(getBaseContext(), getString(R.string.key_preference_enabled_notifications_vibration),
                getResources().getBoolean(R.bool.preference_enabled_notifications_vibration_default))) {
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        }

        final PendingIntent pendingIntent = createPendingIntent(intent);
        if(pendingIntent != null) {
            mBuilder.setContentIntent(pendingIntent);
        }

        final Object service = getSystemService(Context.NOTIFICATION_SERVICE);
        if(service == null) {
            return;
        }

        final NotificationManager mNotificationManager = (NotificationManager) service;
        mNotificationManager.notify(id, mBuilder.build());
    }

    private PendingIntent createPendingIntent(final Intent intent) {
        if(intent == null) {
            return null;
        }

        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(intent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
