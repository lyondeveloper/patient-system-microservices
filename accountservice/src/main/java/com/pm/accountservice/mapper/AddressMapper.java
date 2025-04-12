package com.pm.accountservice.mapper;

import com.pm.accountservice.dto.address.AddressRequestDTO;
import com.pm.accountservice.dto.address.AddressResponseDTO;
import com.pm.accountservice.model.Address;

import java.util.Optional;
import java.util.UUID;

public class AddressMapper {

    public static AddressResponseDTO transformToDto(Address address) {
        return AddressResponseDTO.builder()
                .id(address.getId().toString())
                .city(address.getCity())
                .street(address.getStreet())
                .state(address.getState())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .build();
    }

    public static Address transformToModel(AddressRequestDTO addressRequestDTO) {
        return Address.builder()
                .city(addressRequestDTO.getCity())
                .street(addressRequestDTO.getStreet())
                .state(addressRequestDTO.getState())
                .country(addressRequestDTO.getCountry())
                .zipCode(addressRequestDTO.getZipCode())
                .build();
    }
}
