package com.pm.accountservice.dto.user;

import com.pm.accountservice.dto.address.AddressResponseDTO;
import com.pm.accountservice.dto.tenant.TenantResponseDTO;
import com.pm.accountservice.model.Address;
import com.pm.accountservice.model.Tenant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String role;
    private String phoneNumber;
    private boolean isActive;
    private String email;
    private Tenant tenant;
    private Address address;
}
