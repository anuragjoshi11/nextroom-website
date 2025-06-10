package com.nextroom.app.web.exception;

public class TokenExpiredException extends IllegalArgumentException {

    public TokenExpiredException(String message) {
        super(message);
    }
}
