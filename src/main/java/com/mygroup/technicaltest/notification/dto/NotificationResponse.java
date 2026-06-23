package com.mygroup.technicaltest.notification.dto;

import com.mygroup.technicaltest.notification.model.Notification;

import java.time.Instant;

/**
 * Public representation of a stored notification, returned by both endpoints.
 */
public record NotificationResponse(
        Long id,
        String userId,
        String message,
        String channel,
        Instant createdAt) {

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUserId(),
                notification.getMessage(),
                notification.getChannel().name().toLowerCase(),
                notification.getCreatedAt());
    }
}