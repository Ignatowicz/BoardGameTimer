package com.example.boardgametimer.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.boardgametimer.R;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.ui.main.MainActivity;
import com.example.boardgametimer.ui.notification.AcceptGameActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;


public class NotificationService extends FirebaseMessagingService {

    private final String TAG = "MyFirebaseMsgService";

    LoggedInUser user;

    public NotificationService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            try {
                JSONObject data = new JSONObject(remoteMessage.getData());
                String jsonMessage = data.getString("notification_id");
                Log.d(TAG, "onMessageReceived: \n" +
                        "Extra Information: " + jsonMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle(); //get title
            String message = remoteMessage.getNotification().getBody(); //get message
            String click_action = remoteMessage.getNotification().getClickAction(); //get notification_id
            String player_id = remoteMessage.getNotification().getTag(); // get player id
            String play_id = remoteMessage.getNotification().getColor(); // get play id

            Log.d(TAG, "Message Notification Title: " + title);
            Log.d(TAG, "Message Notification Body: " + message);
            Log.d(TAG, "Message Notification Id: " + click_action);
            Log.d(TAG, "Message Notification Player Id: " + player_id);
            Log.d(TAG, "Message Notification Play Id: " + play_id);

            sendNotification(title, message, click_action, player_id, play_id);
        }
    }

    @Override
    public void onDeletedMessages() {

    }

    private void sendNotification(String title, String messageBody, String click_action, String player_id, String play_id) {
        Intent intent;
        if (click_action.equals("1")) {
            intent = new Intent(this, AcceptGameActivity.class);
            intent.putExtra("playerId", player_id);
            intent.putExtra("playId", play_id);
            intent.putExtra("title", title);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        } else if (click_action.equals("MAINACTIVITY")) {
//            intent = new Intent(this, MainActivity.class);
//            intent.putExtra("playerId", player_id);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("playerId", player_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    // maybe there is noo need to use tokens..
//    /**
//     * Called if InstanceID token is updated. This may occur if the security of
//     * the previous token had been compromised. Note that this is called when the InstanceID token
//     * is initially generated so this is where you would retrieve the token.
//     */
//    @Override
//    public void onNewToken(String token) {
//        Log.d(TAG, "Refreshed token: " + token);
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // Instance ID token to your app server.
//        sendRegistrationToServer(token);
//    }
//
//    private void sendRegistrationToServer(String token) {
//        // TODO: Implement this method to send token to your app server.
//    }
}