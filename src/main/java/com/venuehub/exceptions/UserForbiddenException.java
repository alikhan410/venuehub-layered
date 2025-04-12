package com.venuehub.exceptions;

import org.springframework.http.HttpStatus;

public class UserForbiddenException extends RuntimeException implements CustomExcpetion {
    private final int status = HttpStatus.FORBIDDEN.value();
    private final HttpStatus error = HttpStatus.FORBIDDEN;
    private final String message;

    public UserForbiddenException(String message) {
        super(message);
        this.message = message;
    }

    public UserForbiddenException() {
        this("User is forbidden to perform this action");
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
