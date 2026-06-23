package com.mygroup.technicaltest.notification;

import com.mygroup.technicaltest.notification.exception.ProviderNotFoundException;
import com.mygroup.technicaltest.notification.model.Channel;
import com.mygroup.technicaltest.notification.provider.EmailNotificationProvider;
import com.mygroup.technicaltest.notification.provider.NotificationProvider;
import com.mygroup.technicaltest.notification.provider.NotificationProviderFactory;
import com.mygroup.technicaltest.notification.provider.SmsNotificationProvider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for the Strategy + Factory channel dispatch.
 */
class NotificationProviderFactoryTest {

    private final NotificationProviderFactory factory = new NotificationProviderFactory(
            List.of(new EmailNotificationProvider(), new SmsNotificationProvider()));

    @Test
    void returnsEmailProviderForEmailChannel() {
        NotificationProvider provider = factory.getProvider(Channel.EMAIL);
        assertThat(provider.getChannel()).isEqualTo(Channel.EMAIL);
    }

    @Test
    void returnsSmsProviderForSmsChannel() {
        NotificationProvider provider = factory.getProvider(Channel.SMS);
        assertThat(provider.getChannel()).isEqualTo(Channel.SMS);
    }

    @Test
    void throwsWhenNoProviderRegistered() {
        NotificationProviderFactory empty = new NotificationProviderFactory(List.of());
        assertThatThrownBy(() -> empty.getProvider(Channel.EMAIL))
                .isInstanceOf(ProviderNotFoundException.class);
    }
}