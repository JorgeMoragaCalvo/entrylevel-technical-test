package com.mygroup.technicaltest.notification.dto;

import jakarta.validation.constraints.NotBlank;

public record NotificationRequest(
        @NotBlank(message = "userId is required") String userId,
        @NotBlank(message = "message is required") String message,
        @NotBlank(message = "channel is required") String channel) {
}