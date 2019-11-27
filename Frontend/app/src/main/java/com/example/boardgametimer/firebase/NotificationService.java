package com.example.boardgametimer.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class NotificationService extends FirebaseMessagingService {

    private final String TAG = "JSA-FCM";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {
            // do with Notification payload...
            // remoteMessage.getNotification().getBody()
            Log.e(TAG, "Title: " + remoteMessage.getNotification().getTitle());
            Log.e(TAG, "Body: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0) {
            // do with Data payload...
            // remoteMessage.getData()
            Log.e(TAG, "Data: " + remoteMessage.getData());
        }
    }
}