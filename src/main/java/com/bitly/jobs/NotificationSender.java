package com.bitly.jobs;

import com.bitly.models.Notification;

public interface NotificationSender {

    void send(Notification notification);
}
