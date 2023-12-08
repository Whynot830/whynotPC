package com.example.whynotpc.utils;

public class NoAuthenticationException extends RuntimeException {
    public NoAuthenticationException(String message) {
        super(message);
    }
    public NoAuthenticationException() {
        this("");
    }
}
