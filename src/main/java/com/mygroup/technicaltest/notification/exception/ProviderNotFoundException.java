package com.mygroup.technicaltest.notification.exception;

import com.mygroup.technicaltest.notification.model.Channel;

/**
 * Raised when no {@link com.mygroup.technicaltest.notification.provider.NotificationProvider}
 * is registered for a given channel. Indicates a configuration problem rather than bad input.
 */
public class ProviderNotFoundException extends RuntimeException {

    public ProviderNotFoundException(Channel channel) {
        super("No notification provider registered for channel: " + channel + ".");
    }
}