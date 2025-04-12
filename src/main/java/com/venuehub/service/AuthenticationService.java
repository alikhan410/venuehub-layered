package com.venuehub.service;

import com.venuehub.dto.LoginResponse;
import com.venuehub.dto.UserDto;
import com.venuehub.entity.User;
import com.venuehub.exceptions.NoSuchUserException;
import com.venuehub.exceptions.UserForbiddenException;
import com.venuehub.exceptions.WrongPasswordException;
import com.venuehub.repository.RoleRepository;
import com.venuehub.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {
    public static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Autowired
    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public LoginResponse registerUser(UserDto userDto) {

        User user = User.builder()
                .username(userDto.username())
                .email(userDto.email())
                .password(passwordEncoder.encode(userDto.password()))
                .firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .authorities(new HashSet<>(List.of(roleRepository.findByAuthority("USER"))))
                .build();

        userRepository.save(user);

        String jwt = tokenService.generateUserJwt(userDto.username(), "USER");

        return new LoginResponse(userDto.username(), jwt);

    }

    public LoginResponse registerVendor(UserDto userDto) {
        User user = User.builder()
                .username(userDto.username())
                .email(userDto.email())
                .password(passwordEncoder.encode(userDto.password()))
                .firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .authorities(new HashSet<>(List.of(roleRepository.findByAuthority("USER"), roleRepository.findByAuthority("VENDOR"))))
                .build();

        userRepository.save(user);
        String jwt = tokenService.generateUserJwt(userDto.username(), "USER VENDOR");
        return new LoginResponse(userDto.username(), jwt);
    }

    public LoginResponse loginUser(String username, String password) {
        userRepository.findByUsername(username).orElseThrow(NoSuchUserException::new);

        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));

            //e.g Output: "USER ADMIN"
            String scope = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

            if(!scope.contains("USER")) throw new UserForbiddenException("Please register as a user first");

            String jwt = tokenService.generateUserJwt(username, scope);

            return new LoginResponse(username, jwt);
        } catch (AuthenticationException e) {
            throw new WrongPasswordException();
        }

    }

    public LoginResponse loginVendor(String username, String password) {
        userRepository.findByUsername(username).orElseThrow(NoSuchUserException::new);

        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));

            //e.g Output: "USER ADMIN VENDOR"
            String scope = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

            if(!scope.contains("VENDOR")) throw new UserForbiddenException("Please register as a vendor first");

            String jwt = tokenService.generateVendorJwt(username, scope);

            return new LoginResponse(username, jwt);
        } catch (AuthenticationException e) {
            throw new WrongPasswordException();
        }

    }
}
