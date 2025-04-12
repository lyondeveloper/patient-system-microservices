package com.pm.accountservice.controller;

import com.pm.accountservice.dto.user.UserRequestDTO;
import com.pm.accountservice.dto.user.UserResponseDTO;
import com.pm.accountservice.model.User;
import com.pm.accountservice.service.CommonService;
import com.pm.accountservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts/users")
@Tag(name = "User", description = "API for managing users")
public class UserController {

    private final UserService userService;
    private final CommonService commonService;

    public UserController(UserService userService, CommonService commonService) {
        this.userService = userService;
        this.commonService = commonService;
    }

    @Operation(summary = "Get current user logged in")
    @GetMapping("/currentUserAuthenticated")
    public ResponseEntity<UserResponseDTO> getCurrentUserAuthenticated() {
        return userService.getCurrentUserAuthenticated()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsersByTenantId() {
        var usersByTenantId = userService.getAllUsersByTenantId();

        return ResponseEntity.ok().body(usersByTenantId);
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String userId) {
        var userFound = userService.getUserById(userId);

        return ResponseEntity.ok().body(userFound);
    }

    @Operation(summary = "Create a user (Only ADMIN)")
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(@Validated @RequestBody UserRequestDTO userRequestDTO) {
        var userCreated = userService.createUser(userRequestDTO);

        return ResponseEntity.ok().body(userCreated);
    }
}
