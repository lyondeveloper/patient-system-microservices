package com.pm.accountservice.service;

import com.pm.accountservice.dto.auth.LoginRequestDTO;
import com.pm.accountservice.exceptions.users.InvalidCredentialsException;
import com.pm.accountservice.exceptions.users.UserNotFound;
import com.pm.accountservice.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserService userService,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Mono<String> authenticate(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        // Getting a user from the userservice
        // filter by password matching
        // generating a token to return the authenticated user
        return userService
                .findUserByEmail(loginRequestDTO.getEmail())
                .switchIfEmpty(Mono.error(new UserNotFound("User not found with this email")))
                .flatMap(u -> {
                    if (passwordEncoder.matches(loginRequestDTO.getPassword(), u.getPassword())) {
                        return Mono.just(jwtUtil.generateToken(u.getEmail(), u.getRole(), u.getTenantId()));
                    }

                    return Mono.error(new InvalidCredentialsException("Invalid credentials, try again"));
                })
                .doOnError(e -> log.error("Authentication failed", e));
    }
}
