package com.studio2.bgt.notification;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class NotificationManager {

    private AndroidPushNotificationsService androidPushNotificationsService = new AndroidPushNotificationsService();

    public String prepareNotification(Long notificationId, String topic, String title, String description, Map<String, String> friends, String playId) {

        // body
        JSONObject body = new JSONObject();
        body.put("to", "/topics/" + topic);
        body.put("priority", "high");

        // notification
        JSONObject notification = new JSONObject();
        notification.put("title", title);
        notification.put("body", description);
        notification.put("click_action", notificationId);
        notification.put("tag", topic.substring(7)); // get player id
        notification.put("color", playId);
        body.put("notification", notification);

        // data
        JSONObject data = new JSONObject();
        for (Map.Entry<String, String> entry : friends.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            data.put(key, value);
        }
        body.put("data", data);

        return body.toString();
    }

    public List<String> sendNotification(Long notificationId, Set<String> topics, String title, String description, Map<String, String> friends, String playId) throws JSONException {

        List<String> firebaseResponse = new ArrayList<>();

        for (String topic : topics) {
            String bodyJson = prepareNotification(notificationId, topic, title, description, friends, playId);

            HttpEntity<String> request = new HttpEntity<>(bodyJson);

            CompletableFuture<String> pushNotification = androidPushNotificationsService.send(request);
            CompletableFuture.allOf(pushNotification).join();

            try {
                firebaseResponse.add(pushNotification.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        if (firebaseResponse.isEmpty()) {
            return Collections.singletonList("Push Notification ERROR! BAD REQUEST!");
        }
        return firebaseResponse;
    }

}
