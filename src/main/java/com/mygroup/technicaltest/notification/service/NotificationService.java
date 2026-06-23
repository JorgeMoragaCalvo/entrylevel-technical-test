package com.mygroup.technicaltest.notification.service;

import com.mygroup.technicaltest.notification.dto.NotificationRequest;
import com.mygroup.technicaltest.notification.dto.NotificationResponse;
import com.mygroup.technicaltest.notification.model.Channel;
import com.mygroup.technicaltest.notification.model.Notification;
import com.mygroup.technicaltest.notification.provider.NotificationProvider;
import com.mygroup.technicaltest.notification.provider.NotificationProviderFactory;
import com.mygroup.technicaltest.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;


@Service
public class NotificationService {

    private final NotificationProviderFactory providerFactory;
    private final NotificationRepository repository;

    public NotificationService(NotificationProviderFactory providerFactory,
                               NotificationRepository repository) {
        this.providerFactory = providerFactory;
        this.repository = repository;
    }

    /**
     * Sends a notification through the channel-specific provider and stores it in the history.
     *
     * @param request the validated request body
     * @return the persisted notification
     */
    @Transactional
    public NotificationResponse create(NotificationRequest request) {
        Channel channel = Channel.fromValue(request.channel());
        NotificationProvider provider = providerFactory.getProvider(channel);

        Notification notification = Notification.builder()
                .userId(request.userId())
                .message(request.message())
                .channel(channel)
                .createdAt(Instant.now())
                .build();

        provider.send(notification);

        return NotificationResponse.from(repository.save(notification));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> list() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(NotificationResponse::from)
                .toList();
    }
}