package com.venuehub.exceptions;

import org.springframework.http.HttpStatus;

public class NotAuthorizedException extends RuntimeException implements CustomExcpetion {
    private final int status = HttpStatus.UNAUTHORIZED.value();
    private final HttpStatus error = HttpStatus.UNAUTHORIZED;
    private final String message;

    public NotAuthorizedException(String message) {
        super(message);
        this.message = message;
    }

    public NotAuthorizedException() {
        this("User is not authorized");
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

