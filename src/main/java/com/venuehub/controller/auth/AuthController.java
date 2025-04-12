package com.venuehub.controller.auth;

import com.nimbusds.jose.jwk.JWKSet;
import com.venuehub.dto.CurrentUserDto;
import com.venuehub.exceptions.UserForbiddenException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Tag(name = "Auth Info", description = "Exposes public key and info about current-user")
@RestController
public class AuthController implements AuthApi {
    public static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private final JWKSet jwkSet;

    @Autowired
    public AuthController(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String,Object> getPublicKey() {
        return jwkSet.toJSONObject();
    }

    @GetMapping("/current-user")
    public CurrentUserDto getUser(@AuthenticationPrincipal Jwt jwt) {

        String rolesString = jwt.getClaim("roles");
        List<String> roles = Arrays.stream(rolesString.split(" ")).toList();

        if (!roles.contains("USER")) {
            throw new UserForbiddenException();
        }
        return new CurrentUserDto(jwt.getSubject(), roles, jwt.getClaim("loggedInAs"));

    }
}
