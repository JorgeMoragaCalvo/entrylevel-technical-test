package com.mygroup.technicaltest.notification.controller;

import com.mygroup.technicaltest.notification.dto.DataResponse;
import com.mygroup.technicaltest.notification.dto.NotificationRequest;
import com.mygroup.technicaltest.notification.dto.NotificationResponse;
import com.mygroup.technicaltest.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /** Sends a notification and stores it. Returns 201 with the created record. */
    @PostMapping
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Returns the notification history wrapped in a {@code data} envelope. */
    @GetMapping
    public DataResponse<NotificationResponse> list() {
        return DataResponse.of(notificationService.list());
    }
}