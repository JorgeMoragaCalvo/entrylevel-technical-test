package com.mygroup.technicaltest.notification.provider;

import com.mygroup.technicaltest.notification.model.Channel;
import com.mygroup.technicaltest.notification.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SMS channel strategy. This is a stub that logs the delivery; integrating a real SMS
 * gateway would only require changing this class.
 */
@Slf4j
@Component
public class SmsNotificationProvider implements NotificationProvider {

    @Override
    public Channel getChannel() {
        return Channel.SMS;
    }

    @Override
    public void send(Notification notification) {
        log.info("Sending SMS notification to user {}: {}",
                notification.getUserId(), notification.getMessage());
    }
}