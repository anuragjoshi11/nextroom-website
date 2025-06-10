package com.nextroom.app.web.exception;

public class UserNotLinkedToTokenException extends IllegalStateException {

    public UserNotLinkedToTokenException(String message) {
        super(message);
    }
}