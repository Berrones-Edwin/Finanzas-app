package com.bitly.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.bitly.enums.NotificationType;
import com.bitly.models.Notification;

public class NotificationSpecification {

    public static Specification<Notification> hasUserEmail(String email) {
        return (root, query, cb) -> cb.equal(root.get("user").get("email"), email);
    }

    public static Specification<Notification> isUnread(Boolean unread) {
        if (unread == null)
            return null;
        return (root, query, cb) -> unread
                ? cb.isNull(root.get("readAt"))
                : cb.isNotNull(root.get("readAt"));
    }

    public static Specification<Notification> hasType(NotificationType type) {
        if (type == null)
            return null;
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }
}
