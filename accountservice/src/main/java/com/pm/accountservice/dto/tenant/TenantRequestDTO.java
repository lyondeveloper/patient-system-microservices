package com.pm.accountservice.dto.tenant;

import com.pm.accountservice.model.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantRequestDTO {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot be exceed 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must me a valid address")
    private String email;

    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private Address address;
}
