package com.dondeestanapp;

import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
            title = remoteMessage.getNotification().getTitle();
            description = remoteMessage.getNotification().getBody();

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
    }

    private Intent createIntentNotification(String title, String description) {
        Intent intentNotification = new Intent();
        intentNotification.setAction(ACTION_NEW_NOTIFICATION);
        intentNotification.putExtra("title", title);
        intentNotification.putExtra("description", description);

        return intentNotification;
    }
}

