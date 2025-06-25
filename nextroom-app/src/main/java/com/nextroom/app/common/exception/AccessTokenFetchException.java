package com.nextroom.app.common.exception;

public class AccessTokenFetchException extends RuntimeException {
    public AccessTokenFetchException(String message) {
        super(message);
    }
}