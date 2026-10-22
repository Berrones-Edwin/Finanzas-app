package com.bitly.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitly.dtos.NotificationResponse;
import com.bitly.dtos.PageResponse;
import com.bitly.models.Notification;
import com.bitly.repository.NotificationRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            final NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getNotifications(String email, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notification> result = notificationRepository.findByUserEmail(email, pageable);

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

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getType(),
                n.getMessage(),
                n.getStatus(),
                n.getCreatedAt(),
                n.getReadAt());
    }
}
