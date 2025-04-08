package com.pm.accountservice.exceptions.users;

public class UserProcessingException extends RuntimeException {
    public UserProcessingException(String message) {
        super(message);
    }
}
