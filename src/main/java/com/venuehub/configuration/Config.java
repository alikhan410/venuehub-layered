package com.venuehub.configuration;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.venuehub.exceptions.CustomAuthorizationException;
import com.venuehub.utils.RSAKeyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import java.util.UUID;

@Configuration
public class Config {

    private final RSAKeyProperties keys;

    private final CustomAuthorizationException customAuthenticationException;

    @Autowired
    public Config(RSAKeyProperties keys, CustomAuthorizationException customAuthenticationException) {
        this.keys = keys;
        this.customAuthenticationException = customAuthenticationException;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);
        daoProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(daoProvider);
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/**").permitAll()
//                        .requestMatchers("/auth/v3/**").permitAll()
                        .requestMatchers("/v2/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        //auth
                        .requestMatchers(HttpMethod.GET, "/user/logout").authenticated()
                        .requestMatchers(HttpMethod.GET, "/vendor/logout").authenticated()
                        .requestMatchers("/user/**").permitAll()
                        .requestMatchers("/vendor/**").permitAll()
                        //venues
//                        .requestMatchers("/venue/v3/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/images/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/venue/**").permitAll()
                        .requestMatchers(new RegexRequestMatcher("/venue/\\d+/image-0", "GET")).permitAll()
                        .requestMatchers(HttpMethod.GET, "/venue").permitAll()
                        .requestMatchers(HttpMethod.POST, "/venue/**").authenticated()
                        //booking
//                        .requestMatchers("/booking/v3/api-docs").permitAll()
                        .requestMatchers(new RegexRequestMatcher("/bookings/venue/\\d+", "GET")).permitAll()
                        .requestMatchers(new RegexRequestMatcher("/bookings/venue/\\d+", "POST")).authenticated()
                        .requestMatchers(new RegexRequestMatcher("/bookings/\\d+", "PUT")).authenticated()
                        .requestMatchers(new RegexRequestMatcher("/bookings/\\d+", "DELETE")).authenticated()
                        .requestMatchers(new RegexRequestMatcher("/bookings/\\d+", "GET")).authenticated()
                        .requestMatchers(HttpMethod.GET, "/bookings/user").authenticated()
                        .requestMatchers("/.well-known/jwks.json").permitAll()
                        .anyRequest().authenticated()

                )
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(j ->
                                        jwtAuthenticationConverter()
                                )
                                .authenticationEntryPoint(customAuthenticationException)
                );

        return http.build();
    }

    @Bean
    public JWKSet jwkSet() {

        RSAKey rsaKey = new RSAKey.Builder(keys.getPublicKey())
                .privateKey(keys.getPrivateKey())
                .keyID(UUID.randomUUID().toString())
                .build();

        return new JWKSet(rsaKey);

    }


    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(keys.getPublicKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet());
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtConverter;
    }

}
