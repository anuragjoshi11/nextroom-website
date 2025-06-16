package com.nextroom.app.common.exception;

public class InvalidTokenException extends IllegalArgumentException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
