package com.mygroup.technicaltest.notification.dto;

import com.mygroup.technicaltest.notification.model.Notification;

import java.time.Instant;

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