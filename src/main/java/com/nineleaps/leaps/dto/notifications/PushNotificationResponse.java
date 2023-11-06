package com.nineleaps.leaps.dto.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PushNotificationResponse {
    private int status;
    private String message;
}
