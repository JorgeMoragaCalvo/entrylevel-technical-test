package com.mygroup.technicaltest.notification.repository;

import com.mygroup.technicaltest.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Persistence for the notification history. Provides save (inherited) and an ordered
 * listing used by {@code GET /notifications}.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /** Returns the full history, newest send first. */
    List<Notification> findAllByOrderByCreatedAtDesc();
}