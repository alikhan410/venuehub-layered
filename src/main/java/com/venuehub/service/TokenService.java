package com.venuehub.service;

import com.venuehub.controller.auth.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenService {
    public static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final JwtEncoder encoder;

    @Autowired
    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

//    public String generateUserJwt(Authentication auth) {
//
//        Instant now = Instant.now();
//
//        // Output: "USER ADMIN VENDOR"
//        String scope = auth.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(" "));
//
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .issuedAt(now)
//                .issuer("self")
//                .subject(auth.getName())
//                .claim("roles", scope)
//                .claim("loggedInAs", "USER")
//                .build();
//
//        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
//
//    }
    public String generateUserJwt(String username, String scope) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .issuer("self")
                .subject(username)
                .claim("roles", scope)
                .claim("loggedInAs", "USER")
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

    }
    public String generateVendorJwt(String username, String scope) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .issuer("self")
                .subject(username)
                .claim("roles", scope)
                .claim("loggedInAs", "VENDOR")
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

    }

//    public String generateVendorJwt(Authentication auth) {
//        Instant now = Instant.now();
//
//        // Output: "USER ADMIN VENDOR"
//        String scope = auth.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(" "));
//
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .issuedAt(now)
//                .issuer("self")
//                .subject(auth.getName())
//                .claim("roles", scope)
//                .claim("loggedInAs", "VENDOR")
//                .build();
//
//        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
//    }

//    public String generateVendorJwt(String username, String scope) {
//        Instant now = Instant.now();
//
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .issuedAt(now)
//                .issuer("self")
//                .subject(username)
//                .claim("roles", scope)
//                .claim("loggedInAs", "VENDOR")
//                .build();
//
//        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
//
//    }
}
