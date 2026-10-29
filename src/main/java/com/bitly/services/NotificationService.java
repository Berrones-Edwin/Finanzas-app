package com.bitly.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitly.dtos.NotificationResponse;
import com.bitly.dtos.PageResponse;
import com.bitly.enums.NotificationType;
import com.bitly.models.Notification;
import com.bitly.repository.NotificationRepository;
import com.bitly.specifications.NotificationSpecification;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j 
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            final NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getNotifications(
            String email,
            int page,
            int size,
            Boolean unread,
            NotificationType type) {

        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size < 1 || size > 50) {
            throw new IllegalArgumentException("Size must be between 1 and 50");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<Notification> spec = NotificationSpecification.hasUserEmail(email);

        if (unread != null) {
            spec = spec.and(NotificationSpecification.isUnread(unread));
        }

        if (type != null) {
            spec = spec.and(NotificationSpecification.hasType(type));
        }

        Page<Notification> result = notificationRepository.findAll(spec, pageable);

        List<NotificationResponse> content = result.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()

        );
    }

    @Transactional
    public NotificationResponse markAsRead(String email, Long id) {
        Notification n = notificationRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id " + id));

        if (n.getReadAt() == null) {
            n.setReadAt(LocalDateTime.now());
        }

        return toResponse(n);
    }

    @Transactional
    public int markAllAsRead(String email) {
        return notificationRepository.markAllAsRead(email, LocalDateTime.now());
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getType(),
                n.getMessage(),
                n.getStatus(),
                n.getCreatedAt(),
                n.getReadAt());
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(String email) {

        return notificationRepository.countByUserEmailAndReadAtIsNull(email);
    }

    @Transactional
    public void deleteNotification(String username, long id) {
        Notification notification = notificationRepository.findByIdAndUserEmail(null, username)
                .orElseThrow(() -> new EntityNotFoundException("Notification was not found with id " + id));
        
        notificationRepository.delete(notification);


    }
}
