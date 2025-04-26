package com.pm.accountservice.controller;

import com.pm.accountservice.dto.user.UserRequestDTO;
import com.pm.accountservice.dto.user.UserResponseDTO;
import com.pm.accountservice.exceptions.users.CreateNewUserException;
import com.pm.accountservice.exceptions.users.UserProcessingException;
import com.pm.accountservice.service.CommonService;
import com.pm.accountservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/accounts/users")
@Tag(name = "User", description = "API for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get current user logged in")
    @GetMapping("/currentUserAuthenticated")
    public Mono<ResponseEntity<UserResponseDTO>> getCurrentUserAuthenticated() {
        return userService.getCurrentUserAuthenticated()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
                .onErrorResume(error -> {
                    log.error("Unexpected 500 server error: {}", error.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public ResponseEntity<Flux<UserResponseDTO>> getAllUsersByTenantId() {
        return ResponseEntity.ok(userService.findAllUsersByTenantId());
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponseDTO>> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Create a user (Only ADMIN)")
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<UserResponseDTO>> createUser(@Validated @RequestBody UserRequestDTO userRequestDTO) {
        log.info("Creating a new user: {}", userRequestDTO);
        return userService.createUser(userRequestDTO)
                .map(ResponseEntity::ok);
    }
}
