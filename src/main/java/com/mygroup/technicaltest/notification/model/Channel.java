package com.mygroup.technicaltest.notification.model;

import com.mygroup.technicaltest.notification.exception.InvalidChannelException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Supported notification channels. The {@code channel} request parameter is matched
 * against these values (case-insensitively) via {@link #fromValue(String)}.
 */
public enum Channel {
    EMAIL,
    SMS;

    /**
     * Resolves a {@link Channel} from its textual representation, ignoring a case.
     *
     * @param value the raw channel value coming from the request
     * @return the matching channel
     * @throws InvalidChannelException if the value is null or does not match a channel
     */
    public static Channel fromValue(String value) {
        if (value == null) {
            throw new InvalidChannelException(null, supportedValues());
        }
        return Arrays.stream(values())
                .filter(channel -> channel.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new InvalidChannelException(value, supportedValues()));
    }

    private static String supportedValues() {
        return Arrays.stream(values())
                .map(channel -> channel.name().toLowerCase())
                .collect(Collectors.joining(", "));
    }
}