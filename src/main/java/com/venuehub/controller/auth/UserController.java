package com.venuehub.controller.auth;

import com.venuehub.dto.LoginDto;
import com.venuehub.dto.UserDto;
import com.venuehub.dto.LoginResponse;
import com.venuehub.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


//@Api(value = "User Controller", tags = {"User Login Api"})
@Tag(name = "User Login", description = "Let's user register/login/logout")
@RestController
@Validated
public class UserController implements UserApi{
    public static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final AuthenticationService authenticationService;

    @Autowired
    public UserController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/user/register")
    public ResponseEntity<LoginResponse> registerUser(@RequestBody @Valid UserDto body) {
        LoginResponse response = authenticationService.registerUser(body);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/user/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginDto body) {

        LoginResponse response = authenticationService.loginUser(body.username(), body.password());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/logout")
    public ResponseEntity<LoginResponse> logoutUser(@AuthenticationPrincipal Jwt jwt) {
        LoginResponse response = new LoginResponse(jwt.getSubject(), null);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
