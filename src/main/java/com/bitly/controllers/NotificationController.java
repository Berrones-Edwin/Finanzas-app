package com.bitly.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bitly.dtos.MarkAllReadResponse;
import com.bitly.dtos.NotificationResponse;
import com.bitly.dtos.PageResponse;
import com.bitly.dtos.UnreadCountResponse;
import com.bitly.enums.NotificationType;
import com.bitly.services.NotificationService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/v1/notifications")
@RestController
@Tag(name = "Notification")
public class NotificationController {

        private final NotificationService notificationService;

        public NotificationController(
                        final NotificationService notificationService) {
                this.notificationService = notificationService;
        }

        @GetMapping
        public ResponseEntity<PageResponse<NotificationResponse>> getAllNotifications(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
                        @RequestParam(name = "size", defaultValue = "10", required = false) int size,
                        @RequestParam(required = false) Boolean unread,
                        @RequestParam(required = false) NotificationType type) {

                return ResponseEntity.ok(
                                notificationService.getNotifications(userDetails.getUsername(), page, size, unread,
                                                type));

        }

        @GetMapping("/unread-count")
        public ResponseEntity<UnreadCountResponse> getUnreadCount(
                        @AuthenticationPrincipal UserDetails userDetails) {

                return ResponseEntity.ok(
                                new UnreadCountResponse(
                                                notificationService.getUnreadCount(userDetails.getUsername())));
        }

        @PatchMapping("/read-all")
        public ResponseEntity<MarkAllReadResponse> markAllAsRead(
                        @AuthenticationPrincipal UserDetails userDetails) {

                return ResponseEntity.ok(
                                new MarkAllReadResponse(
                                                notificationService.markAllAsRead(userDetails.getUsername())));
        }

        @PatchMapping("/{id}/read")
        public ResponseEntity<NotificationResponse> readNotification(
                        @PathVariable("id") long id,
                        @AuthenticationPrincipal UserDetails userDetails) {
                return ResponseEntity.ok(
                                notificationService.markAsRead(userDetails.getUsername(), id));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteNotification(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @PathVariable("notificationId") long notificationId) {

                notificationService.deleteNotification(userDetails.getUsername(), notificationId);

                return ResponseEntity.noContent().build();
        }

}
