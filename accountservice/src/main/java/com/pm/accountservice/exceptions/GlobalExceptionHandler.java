package com.pm.accountservice.exceptions;

import com.pm.accountservice.exceptions.address.AddressExceptions;
import com.pm.accountservice.exceptions.tenants.TenantNameAlreadyExistsException;
import com.pm.accountservice.exceptions.tenants.TenantNotFoundException;
import com.pm.accountservice.exceptions.users.CreateNewUserException;
import com.pm.accountservice.exceptions.users.InvalidCredentialsException;
import com.pm.accountservice.exceptions.users.UserNotFound;
import com.pm.accountservice.exceptions.users.UserProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Manejo de errores de validación en WebFlux
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationExceptions(WebExchangeBindException ex) {
        Map<String, Object> errors = new HashMap<>();

        // Extraer todos los mensajes de error de validación
        Map<String, String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Optional.ofNullable(fieldError.getDefaultMessage())
                                .orElse("")
                ));

        errors.put("errors", validationErrors);
        errors.put("message", "Validation failed");
        errors.put("status", HttpStatus.BAD_REQUEST.value());

        log.error("Validation error: {}", validationErrors);

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleAllExceptions(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        log.error("Unexpected error: {}", ex.getMessage());
        response.put("message", "An unexpected error occurred: " + ex.getMessage());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleInvalidCredentials(InvalidCredentialsException ex) {
        Map<String, Object> response = new HashMap<>();
        log.error("Invalid credentials: {}", ex.getMessage());
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> response = new HashMap<>();
        log.error("Access denied: {}", ex.getMessage());
        response.put("message", "Access Denied: " + ex.getMessage());
        response.put("status", HttpStatus.FORBIDDEN.value());
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(response));
    }

    @ExceptionHandler(CreateNewUserException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleCreateNewUserExceptions(CreateNewUserException ex) {
        Map<String, Object> response = new HashMap<>();
        log.error("User Creating failed: {}", ex.getMessage());
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

    @ExceptionHandler(AddressExceptions.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleAddressExceptions(CreateNewUserException ex) {
        Map<String, Object> response = new HashMap<>();
        log.error("Address exception triggered: {}", ex.getMessage());
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }
}
