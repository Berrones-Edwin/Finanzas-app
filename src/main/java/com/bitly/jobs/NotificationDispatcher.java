package com.bitly.jobs;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bitly.enums.NotificationStatus;
import com.bitly.models.Notification;
import com.bitly.repository.NotificationRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationDispatcher {

    private final NotificationRepository notificationRepository;
    private final LoggingNotificationSender loggingNotificationSender;

    private static final int BATCH_SIZE = 50;
    private static final int MAX_ATTEMPTS = 5;

    public NotificationDispatcher(
            final NotificationRepository notificationRepository,
            final LoggingNotificationSender loggingNotificationSender) {

        this.notificationRepository = notificationRepository;
        this.loggingNotificationSender = loggingNotificationSender;
    }

    @Transactional
    @Scheduled(fixedDelay = 30_000)

    public void dispatchPending() {
        List<Notification> pending = notificationRepository.findDispatchable(
                NotificationStatus.PENDING,
                LocalDateTime.now(),
                PageRequest.of(0, BATCH_SIZE));

        for (Notification n : pending) {
            try {
                loggingNotificationSender.send(n);
                n.setStatus(NotificationStatus.SENT);
                n.setSentAt(LocalDateTime.now());
                n.setNextAttemptAt(null);
            } catch (Exception e) {
                log.warn("Failed to send notification={}", n.getId(), e);
                handleFailure(n);

            }
        }
    }

    private void handleFailure(Notification notification) {
        int attempts = notification.getAttempts() + 1;
        notification.setAttempts(attempts);

        if (attempts >= MAX_ATTEMPTS) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setNextAttemptAt(null);
        } else {

            notification.setNextAttemptAt(
                    LocalDateTime.now().plusMinutes(1L << (attempts - 1)));
        }
    }

}
