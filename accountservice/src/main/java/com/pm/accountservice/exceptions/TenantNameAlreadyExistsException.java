package com.pm.accountservice.exceptions;

public class TenantNameAlreadyExistsException extends RuntimeException {
    public TenantNameAlreadyExistsException(String message) {
        super(message);
    }
}
