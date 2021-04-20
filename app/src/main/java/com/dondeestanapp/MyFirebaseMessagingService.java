package com.dondeestanapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dondeestanapp.ui.DialogActivity;
import com.dondeestanapp.ui.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static String TAG = "REMOTE MESSAGE";
    public static String ACTION_NEW_NOTIFICATION = "NEW NOTIFICATION";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        try {
            String title = "";
            String description = "";
            Log.d(TAG, "From: " + remoteMessage.getFrom());

            if (remoteMessage.getNotification() != null) {
                title = remoteMessage.getNotification().getTitle();
                description = remoteMessage.getNotification().getBody();

                Log.d(TAG, "Message Notification Title: " + title);
                Log.d(TAG, "Message Notification Body: " + description);

                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(createIntentNotification(title, description));

            }
        } catch (Exception e) {
            // TODO: Catch the exception
        }
    /*private static String TAG = "REMOTE MESSAGE";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Looper.prepare();

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            Intent intent = new Intent(MyFirebaseMessagingService.this, DialogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("title", remoteMessage.getNotification().getTitle());
            intent.putExtra("description", remoteMessage.getNotification().getBody());
            startActivity(intent);

            //Toast.makeText(this, remoteMessage.getNotification().getBody(), Toast.LENGTH_LONG).show();
        }
        Looper.loop();*/
    }

    /*
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            mostrarNotificacion(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private void mostrarNotificacion(String title, String body){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificacionBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_drive)
                .setContentTitle(title != null ? title : "DondeEstanApp")
                .setContentText(body)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_drive))
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificacionBuilder.build());
    }*/

    private Intent createIntentNotification(String title, String description) {
        Intent intentNotification = new Intent();
        intentNotification.setAction(ACTION_NEW_NOTIFICATION);
        intentNotification.putExtra("title", title);
        intentNotification.putExtra("description", description);

        return intentNotification;
    }
}

