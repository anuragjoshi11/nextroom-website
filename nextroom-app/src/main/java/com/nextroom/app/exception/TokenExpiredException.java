package com.nextroom.app.exception;

public class TokenExpiredException extends IllegalArgumentException {

    public TokenExpiredException(String message) {
        super(message);
    }
}
