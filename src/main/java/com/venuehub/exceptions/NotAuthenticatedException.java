package com.venuehub.exceptions;

import org.springframework.http.HttpStatus;

public class NotAuthenticatedException extends RuntimeException implements CustomExcpetion {

    private final int status = HttpStatus.UNAUTHORIZED.value();
    private final HttpStatus error = HttpStatus.UNAUTHORIZED;
    private final String message;

    public NotAuthenticatedException(String message) {
        super(message);
        this.message = message;
    }

    public NotAuthenticatedException() {
        this("Unable to authenticate, please log in again");
    }

    @Override
    public ErrorResponse getResponse() {
        return new ErrorResponse(status, error, message);
    }

    @Override
    public int getCode() {
        return status;
    }

    @Override
    public HttpStatus getStatus() {
        return error;
    }
}
