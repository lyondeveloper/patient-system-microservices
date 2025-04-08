package com.pm.accountservice.dto.user;

import com.pm.accountservice.model.Address;
import com.pm.accountservice.model.Tenant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    @NotBlank(message = "First Name is required")
    @Size(max = 100, message = "First name cannot be exceed 100 characters")
    private String firstName;

    @NotBlank(message = "First Name is required")
    @Size(max = 100, message = "Last name cannot be exceed 100 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must me a valid address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    private String phoneNumber;
    private boolean isActive;

    // regexp to validate UUID
    @Pattern(
            regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "Address ID must be a valid UUID format"
    )
    private Address addressId;

    private Tenant tenantId;
}
