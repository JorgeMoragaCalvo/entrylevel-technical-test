package com.mygroup.technicaltest.notification.provider;

import com.mygroup.technicaltest.notification.exception.ProviderNotFoundException;
import com.mygroup.technicaltest.notification.model.Channel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory that resolves the {@link NotificationProvider} for a given {@link Channel}.
 *
 * <p>Spring injects every {@link NotificationProvider} bean, which the factory indexes by
 * channel. Adding a new channel is as simple as adding a new provider component — no change
 * is needed here (open/closed principle).
 */
@Component
public class NotificationProviderFactory {

    private final Map<Channel, NotificationProvider> providersByChannel;

    public NotificationProviderFactory(List<NotificationProvider> providers) {
        this.providersByChannel = providers.stream()
                .collect(Collectors.toMap(NotificationProvider::getChannel, Function.identity()));
    }

    /**
     * @param channel the channel to deliver through
     * @return the provider registered for the channel
     * @throws ProviderNotFoundException if no provider handles the channel
     */
    public NotificationProvider getProvider(Channel channel) {
        NotificationProvider provider = providersByChannel.get(channel);
        if (provider == null) {
            throw new ProviderNotFoundException(channel);
        }
        return provider;
    }
}