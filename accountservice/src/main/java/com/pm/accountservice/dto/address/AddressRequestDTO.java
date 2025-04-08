package com.pm.accountservice.dto.address;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AddressRequestDTO {
    @NotBlank(message = "City cannot be blank")
    private String city;
    @NotBlank(message = "Street cannot be blank")
    private String street;
    @NotBlank(message = "State cannot be blank")
    private String state;
    @NotBlank(message = "Country cannot be blank")
    private String country;
    @NotBlank(message = "Zip code cannot be blank")
    private String zipCode;
}
