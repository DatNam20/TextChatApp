package com.example.textchatapp.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.textchatapp.MessageActivity;
import com.example.textchatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "logTAG";


    FirebaseUser firebaseUser;
    DatabaseReference dbReference;

    String notificationReceiver;
    String notificationBody;
    String notificationTitle;
    String messageSender;
    String notificationIcon;


    public MessagingService(){

    }

    @Override
    public void onNewToken(@NonNull String refreshedToken) {
        super.onNewToken(refreshedToken);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.i(TAG, "  MessagingService _  (onNewToken)\n  refreshedToken _  "+refreshedToken);

        if (firebaseUser != null && refreshedToken != null)
            updateToken(refreshedToken);
        else
            Toast.makeText(MessagingService.this, "Error!", Toast.LENGTH_SHORT).show();
    }


    public void updateToken(String refreshedToken) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("Token");

        Token token = new Token(refreshedToken);

        Log.i(TAG, "    MessagingService  -->  (updateToken)\n "+dbReference.child(firebaseUser.getUid()).setValue(token));

        dbReference.child(firebaseUser.getUid()).setValue(token);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        RemoteMessage.Notification remoteNotification = remoteMessage.getNotification();
        Log.i(TAG, "    MessagingService  -->  (onMessageReceived)\n . \tremoteNotification  -->  "+remoteNotification.getBody());

        messageSender = remoteMessage.getData().get("messageSender");

        if (firebaseUser != null && messageSender.equals(firebaseUser.getUid()))
            sendNotification(remoteMessage);
    }


    private void sendNotification(RemoteMessage remoteMessage) {

        notificationReceiver = remoteMessage.getData().get("notificationReceiver");
        notificationBody = remoteMessage.getData().get("notificationBody");
        notificationTitle = remoteMessage.getData().get("notificationTitle");
        notificationIcon = remoteMessage.getData().get("notificationIcon");

        RemoteMessage.Notification remoteNotification = remoteMessage.getNotification();

        int integer = Integer.parseInt(notificationReceiver.replaceAll("[\\D]", ""));

        Intent intent = new Intent(this, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userID", notificationReceiver);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Log.i(TAG, "    MessagingService  -->  (sendNotification) ");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, integer, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationWrapper notificationWrapper = new NotificationWrapper(this);

        Notification.Builder notificationBuilder = notificationWrapper.getNotificationData(notificationTitle, notificationBody,
                                                                    pendingIntent, notificationIcon);

        int newInteger = 0;
        if (integer > 0)
            newInteger = integer;

        notificationWrapper.getNotificationManager().notify(newInteger, notificationBuilder.build());
    }

}