package com.nextroom.app.web.exception;

public class InvalidTokenException extends IllegalArgumentException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
