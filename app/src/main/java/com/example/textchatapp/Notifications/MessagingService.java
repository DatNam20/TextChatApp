package com.example.textchatapp.Notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.textchatapp.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MessagingService extends FirebaseMessagingService {

    FirebaseUser firebaseUser;
    DatabaseReference dbReference;

    String notificationReceiver;
    String notificationBody;
    String notificationTitle;
    String messageSender;
    String notificationIcon;


    @Override
    public void onNewToken(@NonNull String refreshedToken) {
        super.onNewToken(refreshedToken);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null && refreshedToken != null)
            updateToken(refreshedToken);
        else
            Toast.makeText(MessagingService.this, "Error!", Toast.LENGTH_SHORT).show();

    }


    private void updateToken(String refreshedToken) {

        Token token = new Token(refreshedToken);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("Token");
        dbReference.child(firebaseUser.getUid()).setValue(token);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        messageSender = remoteMessage.getData().get("messageSender");

        if (firebaseUser != null && messageSender == firebaseUser.getUid())
            sendNotification(remoteMessage);
    }


    private void sendNotification(RemoteMessage remoteMessage) {

        notificationReceiver = remoteMessage.getData().get("notificationReceiver");
        notificationBody = remoteMessage.getData().get("notificationBody");
        notificationTitle = remoteMessage.getData().get("notificationTitle");
        notificationIcon = remoteMessage.getData().get("notificationIcon");

        int integer = Integer.parseInt(notificationReceiver.replaceAll("[\\D]", ""));

        Intent intent = new Intent(this, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userID", notificationReceiver);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, integer, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(notificationIcon))
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int newInteger = 0;
        if (integer > 0)
            newInteger = integer;

        notificationManager.notify(newInteger, notificationBuilder.build());
    }


}
