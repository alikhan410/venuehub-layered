package com.venuehub.utils;

import com.venuehub.exceptions.UserForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.List;

public class SecurityChecks {
    public static final Logger logger = LoggerFactory.getLogger(SecurityChecks.class);

    public static void vendorCheck(Jwt jwt) {
        String rolesString = jwt.getClaim("roles");
        List<String> roles = Arrays.stream(rolesString.split(" ")).toList();

        logger.info("Performing security checks for the user: {}, loggedInAs: {} with roles {}", jwt.getSubject(), jwt.getClaim("loggedInAs"), roles);

        if (!jwt.getClaim("loggedInAs").equals("VENDOR")) {
            logger.info("Security check failed: User is not logged in as VENDOR");
            throw new UserForbiddenException("Please log in as vendor first");
        }

        if (!roles.contains("VENDOR")) {
            logger.info("Security check failed: User does not have required role i.e VENDOR");
            throw new UserForbiddenException();
        }

    }
    public static void userCheck(Jwt jwt) {
        String rolesString = jwt.getClaim("roles");
        List<String> roles = Arrays.stream(rolesString.split(" ")).toList();

        if (!jwt.getClaim("loggedInAs").equals("USER")) {
            logger.info("Security check failed: User is not logged in as USER");
            throw new UserForbiddenException("Please log in as vendor first");
        }

        if (!roles.contains("USER")) {
            logger.info("Security check failed: User does not have required role i.e USER");
            throw new UserForbiddenException();
        }


    }

}
