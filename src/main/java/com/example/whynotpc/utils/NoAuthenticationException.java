package com.example.whynotpc.utils;

/**
 * Exception thrown to indicate that no authentication information is available.
 */
public class NoAuthenticationException extends RuntimeException {
    /**
     * Constructs a new {@code NoAuthenticationException} with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     */
    public NoAuthenticationException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code NoAuthenticationException} with a default detail message.
     * The default message indicates that no authentication information is available.
     */
    public NoAuthenticationException() {
        this("No authentication");
    }
}
