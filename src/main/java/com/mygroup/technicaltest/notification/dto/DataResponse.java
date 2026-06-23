package com.mygroup.technicaltest.notification.dto;

import java.util.List;

/**
 * Generic envelope that wraps a collection under a {@code data} key, matching the response
 * shape required by {@code GET /notifications}.
 */
public record DataResponse<T>(List<T> data) {

    public static <T> DataResponse<T> of(List<T> data) {
        return new DataResponse<>(data);
    }
}