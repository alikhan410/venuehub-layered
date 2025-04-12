package com.venuehub.controller.auth;

import com.venuehub.dto.LoginDto;
import com.venuehub.dto.UserDto;
import com.venuehub.dto.LoginResponse;
import com.venuehub.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

//@Api(value = "Vendor Controller", tags = {"Vendor Login Api"})
@Tag(name = "Vendor Login", description = "Let's vendor register/login/logout")
@RestController
@Validated
public class VendorController implements VendorApi {
    private final AuthenticationService authenticationService;

    @Autowired
    public VendorController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/vendor/register")
    public ResponseEntity<LoginResponse> registerVendor(@RequestBody @Valid UserDto body) {
        LoginResponse response = authenticationService.registerVendor(body);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/vendor/login")
    public ResponseEntity<LoginResponse> loginVendor(@RequestBody @Valid LoginDto body) {

        LoginResponse response = authenticationService.loginVendor(body.username(), body.password());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/vendor/logout")
    public ResponseEntity<LoginResponse> logoutVendor(@AuthenticationPrincipal Jwt jwt) {

        LoginResponse response = new LoginResponse(jwt.getSubject(), null);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
