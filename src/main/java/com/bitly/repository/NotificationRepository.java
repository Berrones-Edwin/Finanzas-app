package com.bitly.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bitly.enums.NotificationStatus;
import com.bitly.models.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

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

}
