package com.bitly.dtos;

import java.time.LocalDateTime;

import com.bitly.enums.NotificationStatus;
import com.bitly.enums.NotificationType;

public record NotificationResponse(
        Long id,
        NotificationType type,
        String message,
        NotificationStatus status,
        LocalDateTime createdAt,
        LocalDateTime readAt

) {

}
