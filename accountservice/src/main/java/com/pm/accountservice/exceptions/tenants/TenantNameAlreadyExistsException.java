package com.pm.accountservice.exceptions.tenants;

public class TenantNameAlreadyExistsException extends RuntimeException {
    public TenantNameAlreadyExistsException(String message) {
        super(message);
    }
}
