package com.pm.accountservice.controller;

import com.pm.accountservice.dto.auth.LoginRequestDTO;
import com.pm.accountservice.dto.auth.LoginResponseDTO;
import com.pm.accountservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/accounts/auth")
@Tag(name = "Auth", description = "API for managing authorization")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Generate token on login")
    @PostMapping("login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDto) {
        Optional<String> tokenOptional = authService.authenticate(loginRequestDto);
        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenOptional.get();
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    // any request that goes to this endpoint
    // gets the authorization header
    // to check if its valid token
    @Operation(summary = "Validates the generated token")
    @GetMapping("/validateCurrentToken")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader) {
        // Authorization: Bearer {accessToken}

        // check if header exists
        // if authHeader is null, throw false
        // if there is not authHeader with Bearer, throw false
        // if substring(7) on validate token is false, throw false
        // true continue with the process
        if (authHeader == null
                || !authHeader.startsWith("Bearer ")
                || !authService.validateToken(authHeader.substring(7))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok().build();
    }
}
