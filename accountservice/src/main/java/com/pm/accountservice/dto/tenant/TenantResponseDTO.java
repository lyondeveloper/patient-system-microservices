package com.pm.accountservice.dto.tenant;

import com.pm.accountservice.model.Address;
import com.pm.accountservice.model.TenantMedicament;
import com.pm.accountservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

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
