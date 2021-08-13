package com.example.textchatapp.Notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.example.textchatapp.R;


public class NotificationWrapper extends ContextWrapper {

    private static final String CHANNEL_ID = Integer.toString(R.string.default_notification_channel_id);
    private static final String CHANNEL_NAME = Integer.toString(R.string.default_notification_channel_name);

    private NotificationManager notificationManager;


    public NotificationWrapper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel();
        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

        notificationChannel.enableLights(false);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getNotificationManager().createNotificationChannel(notificationChannel);
    }


    public NotificationManager getNotificationManager() {

        if (notificationManager == null)
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        return  notificationManager;
    }


    @TargetApi(Build.VERSION_CODES.O)
    public  Notification.Builder getNotificationData( String notificationTitle, String notificationBody,
                                                      PendingIntent pendingIntent, String notificationIcon) {

        Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                                                            .setContentIntent(pendingIntent)
                                                            .setContentTitle(notificationTitle)
                                                            .setContentText(notificationBody)
                                                            .setSmallIcon(Integer.parseInt(notificationIcon))
                                                            .setAutoCancel(true);

        return notificationBuilder;
    }


}
