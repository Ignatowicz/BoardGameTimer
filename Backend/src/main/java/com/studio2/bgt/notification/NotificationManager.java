package com.studio2.bgt.notification;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class NotificationManager {

    private final String TOPIC = "BoardGameTimerTopic";

    private AndroidPushNotificationsService androidPushNotificationsService = new AndroidPushNotificationsService();

    public String prepareNotification(String topic, String title, String description, Map<String, String> friends) {
        JSONObject body = new JSONObject();
        body.put("to", "/topics/" + topic);
        body.put("priority", "high");

        JSONObject notification = new JSONObject();
        notification.put("title", title);
        notification.put("body", description);

        JSONObject data = new JSONObject();

        for (Map.Entry<String, String> entry : friends.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            data.put(key, value);
        }

        body.put("notification", notification);
        body.put("data", data);

        return body.toString();
    }

    public String sendNotification(String topic, String title, String description, Map<String, String> friends) throws JSONException {

        String bodyJson = prepareNotification(topic, title, description, friends);

        HttpEntity<String> request = new HttpEntity<>(bodyJson);

        CompletableFuture<String> pushNotification = androidPushNotificationsService.send(request);
        CompletableFuture.allOf(pushNotification).join();

        try {
            String firebaseResponse = pushNotification.get();

            return firebaseResponse;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return "Push Notification ERROR! BAD REQUEST!";
    }

}
