package com.intelliflow.modules.notification.repository;

import com.intelliflow.modules.notification.domain.NotificationEntity;
import com.intelliflow.modules.notification.domain.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

/**
 * Spring Data JPA Repository interface for NotificationEntity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    Page<NotificationEntity> findByRecipient_IdOrderByCreatedAtDesc(UUID recipientId, Pageable pageable);

    Page<NotificationEntity> findByRecipient_IdAndStatusOrderByCreatedAtDesc(UUID recipientId, NotificationStatus status, Pageable pageable);

    long countByRecipient_IdAndStatus(UUID recipientId, NotificationStatus status);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.status = 'READ', n.readAt = CURRENT_TIMESTAMP WHERE n.recipient.id = :recipientId AND n.status = 'UNREAD'")
    int markAllAsReadForUser(@Param("recipientId") UUID recipientId);

    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.createdAt < :threshold AND n.status = :status")
    int deleteOlderThanAndStatus(@Param("threshold") Instant threshold, @Param("status") NotificationStatus status);
}
