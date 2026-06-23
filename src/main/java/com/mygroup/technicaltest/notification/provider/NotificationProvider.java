package com.mygroup.technicaltest.notification.provider;

import com.mygroup.technicaltest.notification.model.Channel;
import com.mygroup.technicaltest.notification.model.Notification;

/**
 * Strategy for delivering a notification through a specific channel. Each implementation
 * declares the {@link Channel} it handles; the {@link NotificationProviderFactory} selects
 * the right one at runtime.
 */
public interface NotificationProvider {

    /** The channel this provider is responsible for. */
    Channel getChannel();

    /** Delivers the notification through this provider's channel. */
    void send(Notification notification);
}