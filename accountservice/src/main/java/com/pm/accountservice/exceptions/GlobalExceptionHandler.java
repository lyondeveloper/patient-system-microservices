package com.pm.accountservice.exceptions;

import com.pm.accountservice.exceptions.address.AddressExceptions;
import com.pm.accountservice.exceptions.tenants.TenantNameAlreadyExistsException;
import com.pm.accountservice.exceptions.tenants.TenantNotFoundException;
import com.pm.accountservice.exceptions.users.CreateNewUserException;
import com.pm.accountservice.exceptions.users.UserNotFound;
import com.pm.accountservice.exceptions.users.UserProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // excepcion que tira error en validaciones
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // un hash map es una estructura que almacena clave y valor
        // una clave no puede repetirse pero los valores si
        Map<String, String> errors = new HashMap<>();

        // adding field and message to key value for exception
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // adding field and message to key value for exception
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: " + ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        Map<String, String> errors = new HashMap<>();
        // logging for dev to check error
        log.error("Email address already exists {}", ex.getMessage());
        errors.put("message", "Email address already exists");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(TenantNameAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleTenantNameAlreadyExistsException(TenantNameAlreadyExistsException ex) {
        Map<String, String> errors = new HashMap<>();
        // logging for dev to check error
        log.error("Tenant name already exists {}", ex.getMessage());
        errors.put("message", "Tenant name already exists");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFound ex) {
        Map<String, String> errors = new HashMap<>();
        log.error("Error while fetching user {}", ex.getMessage());
        errors.put("message", "This user doesnt exist or belong to this tenant");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(CreateNewUserException.class)
    public ResponseEntity<Map<String, String>> handleCreateNewUserException(CreateNewUserException ex) {
        Map<String, String> errors = new HashMap<>();
        log.error("Error while creating a new user {}", ex.getMessage());
        errors.put("message", "Error while creating a new user");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(TenantNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTenantNotFoundException(TenantNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        log.error("Error while fetching this tenant {}", ex.getMessage());
        errors.put("message", "Tenant does not exist with this ID");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UserProcessingException.class)
    public ResponseEntity<Map<String, String>> handleUserProcessingException(UserProcessingException ex) {
        Map<String, String> errors = new HashMap<>();
        log.error("Error while processing a new user {}", ex.getMessage());
        errors.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(AddressExceptions.class)
    public ResponseEntity<Map<String, String>> handleAddressExceptions(AddressExceptions ex) {
        Map<String, String> errors = new HashMap<>();
        log.error("Error while creating the address {}", ex.getMessage());
        errors.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    // add
    // get tenant error exception
    // get user error exception
    // get address error exception
}
