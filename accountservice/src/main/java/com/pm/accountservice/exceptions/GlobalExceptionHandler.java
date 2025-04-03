package com.pm.accountservice.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        Map<String, String> errors = new HashMap<>();
        // logging for dev to check error
        log.warn("Email address already exists {}", ex.getMessage());
        errors.put("message", "Email address already exists");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(TenantNameAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleTenantNameAlreadyExistsException(TenantNameAlreadyExistsException ex) {
        Map<String, String> errors = new HashMap<>();
        // logging for dev to check error
        log.warn("Tenant name already exists {}", ex.getMessage());
        errors.put("message", "Tenant name already exists");
        return ResponseEntity.badRequest().body(errors);
    }

    // add
    // get tenant error exception
    // get user error exception
    // get address error exception
}
