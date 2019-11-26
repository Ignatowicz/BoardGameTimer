package com.studio2.bgt.model.notification;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PushNotificationResponse {

    private int status;
    private String message;

    public PushNotificationResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
