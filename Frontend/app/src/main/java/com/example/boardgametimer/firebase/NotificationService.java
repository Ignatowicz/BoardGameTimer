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
import com.example.boardgametimer.api.HttpUtils;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.data.model.PlayHelper;
import com.example.boardgametimer.ui.acceptgame.AcceptGameActivity;
import com.example.boardgametimer.ui.game.GameActivity;
import com.example.boardgametimer.ui.gameactualplayer.GameActualPlayerActivity;
import com.example.boardgametimer.ui.gameotherplayer.GameOtherPlayerActivity;
import com.example.boardgametimer.utils.ClearResponseFriends;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class NotificationService extends FirebaseMessagingService {

    private final String TAG = "MyFirebaseMsgService";

    LoggedInUser user;
    PlayHelper play;
    String title;
    String description;
    String click_action;
    String player_id;
    String play_id;

    public NotificationService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if description contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            JSONObject data = new JSONObject(remoteMessage.getData());
            Log.d(TAG, data.toString());
        }

        // Check if description contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle(); //get title
            description = remoteMessage.getNotification().getBody(); //get description
            click_action = remoteMessage.getNotification().getClickAction(); //get notification_id
            player_id = remoteMessage.getNotification().getTag(); // get user id
            play_id = remoteMessage.getNotification().getColor(); // get play id

            Log.d(TAG, "Message Notification Title: " + title);
            Log.d(TAG, "Message Notification Body: " + description);
            Log.d(TAG, "Message Notification Id: " + click_action);
            Log.d(TAG, "Message Notification Player Id: " + player_id);
            Log.d(TAG, "Message Notification Play Id: " + play_id);

            getActualPlayer();
        }
    }

    @Override
    public void onDeletedMessages() {
//        cancelGame();
    }

    private void getActualPlayer() {
        HttpUtils.get("players/" + player_id, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(statusCode + response.toString());

                Gson gson = new Gson();
                JsonElement element = gson.fromJson(response.toString(), JsonElement.class);
                NotificationService.this.user = gson.fromJson(element, LoggedInUser.class);
                ClearResponseFriends.clearResponse(NotificationService.this.user);
                getActualPlay();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println(statusCode + responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println(errorResponse.toString());
            }
        });
    }

    private void getActualPlay() {
        HttpUtils.get("play/" + play_id, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(statusCode + response.toString());

                Gson gson = new Gson();
                JsonElement element = gson.fromJson(response.toString(), JsonElement.class);
                NotificationService.this.play = gson.fromJson(element, PlayHelper.class);

                sendNotification();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println(statusCode + responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println(errorResponse.toString());
            }
        });
    }

    private void sendNotification() {
        Intent intent;
        switch (click_action) {
            case "1":  // invitation to the game
                intent = new Intent(this, AcceptGameActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("play", play);
                intent.putExtra("title", title);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            case "2":  // all the players have accepted the game
                intent = new Intent(this, GameActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            case "3":  // one player has rejected the game
                intent = new Intent(this, GameActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            case "4":  // start turn of the player that received this message
                intent = new Intent(this, GameActualPlayerActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("play", play);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            case "5":  // start turn of the not actual player
                intent = new Intent(this, GameOtherPlayerActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("play", play);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            // all the players have accepted/one of them has rejected - the game
            default:  // other notifications that only inform
                intent = new Intent(this, GameActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(description)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void cancelGame() {
        HttpUtils.get("play/" + play_id + "/rejectGame/" + player_id, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Intent intent = new Intent(NotificationService.this, GameActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println(statusCode + responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println(errorResponse.toString());
            }
        });
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
//        // TO DO: Implement this method to send token to your app server.
//    }
}