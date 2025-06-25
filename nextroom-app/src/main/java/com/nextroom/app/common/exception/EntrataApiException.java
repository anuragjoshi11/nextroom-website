package com.nextroom.app.common.exception;

public class EntrataApiException extends RuntimeException {
    public EntrataApiException(String message) {
        super(message);
    }

    public EntrataApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

