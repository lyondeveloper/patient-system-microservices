package com.pm.accountservice.controller;

import com.pm.accountservice.dto.auth.LoginRequestDTO;
import com.pm.accountservice.dto.auth.LoginResponseDTO;
import com.pm.accountservice.service.AuthService;
import com.pm.accountservice.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/accounts/auth")
@Tag(name = "Auth", description = "API for managing authorization")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Generate token on login")
    @PostMapping("login")
    public Mono<ResponseEntity<LoginResponseDTO>> login(@RequestBody LoginRequestDTO loginRequestDto) {
        return authService.authenticate(loginRequestDto)
                .map(token -> ResponseEntity.ok(new LoginResponseDTO(token)))
                // se usa defaultIsEmpty porque no necesitamos otras operaciones
                // reactivas o complejas
                // este metodo es util para hacer este tipo de operaciones si el flujo reactivo principal falla
                // pues simplemente se devuelve un 401 que no es nada reactivo ni nada complejo
                // switchIfEmpty se usa para seguir mas operaciones reactivas si el principal falla
                .onErrorResume(ex -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    // any request that goes to this endpoint
    // gets the authorization header
    // to check if its valid token
    @Operation(summary = "Validates the generated token")
    @GetMapping("/validateCurrentToken")
    public Mono<ResponseEntity<Object>> validateToken() {
        // Mono.justOrEmpty creates a reactive flow
        // that can contain a value or be empty
        // depending the value you pass on
        // same as just, it takes a value and converts it into
        // a reactive data
        // but is a optional reactive data
        return ReactiveSecurityContextHolder.getContext()
                .doOnNext(context -> log.info("Security context found: {}", context))
                .map(SecurityContext::getAuthentication)
                .doOnNext(auth -> log.info("Authentication found: {}, isAuthenticated: {}",
                        auth.getName(), auth.isAuthenticated()))
                .filter(Authentication::isAuthenticated)
                .map(authentication -> {
                    log.info("User is authenticated: {}", authentication.getName());
                    return ResponseEntity.ok().build();
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("No authentication found or user not authenticated");
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                }))
                .doOnError(ex -> log.error("Error validating token: {}", ex.getMessage()))
                .onErrorResume(ex -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }
}
