package com.pm.accountservice.util;

import lombok.Getter;

@Getter
public enum UserTypes {
    USER_PATIENT("USER_PATIENT"),
    USER_DEFAULT("USER_DEFAULT"),
    USER_NURSE("USER_NURSE"),
    USER_DOCTOR("USER_DOCTOR");

    private final String userType;

    UserTypes(String userType) {
        this.userType = userType;
    }

    public static UserTypes fromName(String name) {
        if (name == null) {
            return null;
        }

        for (UserTypes type : UserTypes.values()) {
            if (type.getUserType().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No valid type: " + name);
    }
}
