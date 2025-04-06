package com.pm.accountservice.util;

public enum UserRoles {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN");

    private final String roleName;

    UserRoles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public static UserRoles fromName(String name) {
        if (name == null) {
            return null;
        }

        for (UserRoles role : UserRoles.values()) {
            if (role.getRoleName().equalsIgnoreCase(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No role found with name: " + name);
    }
}
