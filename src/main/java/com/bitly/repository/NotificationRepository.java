package com.bitly.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bitly.enums.NotificationStatus;
import com.bitly.models.Notification;

public interface NotificationRepository
    extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

  @Query("""
          SELECT n FROM Notification n
          WHERE n.status = :status
            AND (n.nextAttemptAt IS NULL OR n.nextAttemptAt <= :now)
          ORDER BY n.createdAt
      """)
  List<Notification> findDispatchable(
      @Param("status") NotificationStatus status,
      @Param("now") LocalDateTime now,
      Pageable pageable);

  Page<Notification> findByUserEmail(String email, Pageable pageable);

  Optional<Notification> findByIdAndUserEmail(Long id, String email);

  Long countByUserEmailAndReadAtIsNull(String email);

  @Query("""
      UPDATE Notification n
      SET n.readAt = :now
      WHERE n.user.email = :email
      AND n.readAt IS NULL
      AND n.deletedAt IS NULL
  """)
  int markAllAsRead(
      @Param("email") String email,
      @Param("now") LocalDateTime now);

}
