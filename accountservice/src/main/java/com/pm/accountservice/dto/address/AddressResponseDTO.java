package com.pm.accountservice.dto.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {
    private String id;
    private String city;
    private String street;
    private String state;
    private String country;
    private String zipCode;
}
