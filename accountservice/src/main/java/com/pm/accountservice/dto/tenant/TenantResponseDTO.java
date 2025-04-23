package com.pm.accountservice.dto.tenant;

import com.pm.accountservice.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantResponseDTO {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private Address address;
}
