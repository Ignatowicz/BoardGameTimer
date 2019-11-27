package com.studio2.bgt.notification;

import com.studio2.bgt.notification.AndroidPushNotificationsService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class NotificationManager {

    private final String TOPIC = "BoardGameTimerTopic";

    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    public String prepareNotification(String topic, String title, String description, Map<String, String> friends) {
        JSONObject body = new JSONObject();
        body.put("to", "/topics/" + topic);
        body.put("priority", "high");

        JSONObject notification = new JSONObject();
        notification.put("title", title);
        notification.put("body", description);


        Map.Entry<String, String> entry = friends.entrySet().iterator().next();
        String key = entry.getKey();
        String value = entry.getValue();

        JSONObject data = new JSONObject();
        data.put(key, value);


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
