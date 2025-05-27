package com.nextroom.app.exception;

public class UserNotLinkedToTokenException extends IllegalStateException {

    public UserNotLinkedToTokenException(String message) {
        super(message);
    }
}