package com.venuehub.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venuehub.exceptions.response.CustomAuthorizationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthorizationException implements AuthenticationEntryPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthorizationException.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        LOGGER.info("Authentication failed");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
//        if(request.getRequestURI().equals("/current-user")){
//
//            response.getWriter().write(new ObjectMapper().writeValueAsString(new CustomAuthorizationResponse()));
//        }
        response.getWriter().write(new ObjectMapper().writeValueAsString(new CustomAuthorizationResponse()));
    }
}