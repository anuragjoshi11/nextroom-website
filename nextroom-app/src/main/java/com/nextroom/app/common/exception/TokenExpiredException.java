package com.nextroom.app.common.exception;

public class TokenExpiredException extends IllegalArgumentException {

    public TokenExpiredException(String message) {
        super(message);
    }
}
