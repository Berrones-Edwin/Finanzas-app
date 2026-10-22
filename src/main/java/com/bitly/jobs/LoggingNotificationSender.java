package com.bitly.jobs;

import org.springframework.stereotype.Component;

import com.bitly.models.Notification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingNotificationSender implements NotificationSender {

    @Override
    public void send(Notification notification) {
        Long budgetId = notification.getBudget() != null
                ? notification.getBudget().getId()
                : null;

        log.info("Notification sent. userId={}, notificationId={}, budgetId={}, message={}",
                notification.getUser().getId(),
                notification.getId(),
                budgetId,
                notification.getMessage());
    }

}
