package com.venuehub.exceptions;

import org.springframework.http.HttpStatus;

public class GeneralException extends RuntimeException implements CustomExcpetion {
    private final int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    private final HttpStatus error = HttpStatus.INTERNAL_SERVER_ERROR;
    private final String message;

    public GeneralException(String message) {
        super(message);
        this.message = message;
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
