package com.mygroup.technicaltest.notification.provider;

import com.mygroup.technicaltest.notification.model.Channel;
import com.mygroup.technicaltest.notification.model.Notification;


public interface NotificationProvider {

    /** The channel this provider is responsible for. */
    Channel getChannel();

    /** Delivers the notification through this provider's channel. */
    void send(Notification notification);
}