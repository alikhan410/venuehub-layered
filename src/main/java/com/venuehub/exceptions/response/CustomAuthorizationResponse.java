package com.venuehub.exceptions.response;

import com.venuehub.exceptions.ErrorResponse;
import org.springframework.http.HttpStatus;

public class CustomAuthorizationResponse extends ErrorResponse {
    public CustomAuthorizationResponse() {
        super(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, "Please login and try again");
    }
}
