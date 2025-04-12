package com.venuehub.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class BookingUnavailableException extends RuntimeException implements CustomExcpetion {
    private final int status = HttpStatus.BAD_REQUEST.value();
    private final HttpStatus error = HttpStatus.BAD_REQUEST;
    private final String message;

    public BookingUnavailableException(String message) {
        super(message);
        this.message = message;
    }

    public BookingUnavailableException() {
        this("Booking is not available");
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