package com.pm.accountservice.dto.user;

import com.pm.accountservice.dto.address.AddressResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String role;
    private String phoneNumber;
    private String type;
    private String createdDate;
    private boolean isActive;
    private String dateOfBirth;
    private String email;
    private Long tenantId;
    private AddressResponseDTO address;
}
