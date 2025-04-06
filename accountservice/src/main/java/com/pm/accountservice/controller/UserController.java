package com.pm.accountservice.controller;

import com.pm.accountservice.dto.user.UserResponseDTO;
import com.pm.accountservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts/users")
@Tag(name = "User", description = "API for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "get current user logged in")
    @GetMapping("/currentUserAuthenticated")
    public ResponseEntity<UserResponseDTO> getCurrentUserAuthenticated() {
        return userService.getCurrentUserAuthenticated()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @Operation(summary = "Create a user (Only ADMIN)")
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createUser() {
        return ResponseEntity.ok().body("success");
    }
}
