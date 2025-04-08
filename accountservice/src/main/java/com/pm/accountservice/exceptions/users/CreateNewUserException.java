package com.pm.accountservice.exceptions.users;

public class CreateNewUserException extends RuntimeException {
    public CreateNewUserException(String message) {
        super(message);
    }
}
