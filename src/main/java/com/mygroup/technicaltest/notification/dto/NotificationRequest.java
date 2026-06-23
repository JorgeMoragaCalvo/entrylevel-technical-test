package com.mygroup.technicaltest.notification.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for {@code POST /notifications}. All fields are mandatory; the channel value
 * is further validated against the supported {@link com.mygroup.technicaltest.notification.model.Channel}
 * values in the service layer.
 */
public record NotificationRequest(
        @NotBlank(message = "userId is required") String userId,
        @NotBlank(message = "message is required") String message,
        @NotBlank(message = "channel is required") String channel) {
}