package com.mygroup.technicaltest.notification.exception;

/**
 * Raised when the requested {@code channel} value does not map to a supported channel.
 * Handled by the global exception handler and translated into a 400 response.
 */
public class InvalidChannelException extends RuntimeException {

    public InvalidChannelException(String value, String supportedValues) {
        super("Invalid channel '" + value + "'. Supported channels: " + supportedValues + ".");
    }
}