package com.nextroom.app.common.exception;

public class UserNotLinkedToTokenException extends IllegalStateException {

    public UserNotLinkedToTokenException(String message) {
        super(message);
    }
}