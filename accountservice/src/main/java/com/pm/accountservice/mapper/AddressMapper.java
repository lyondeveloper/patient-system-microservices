package com.pm.accountservice.mapper;

import com.pm.accountservice.dto.address.AddressResponseDTO;
import com.pm.accountservice.model.Address;

import java.util.Optional;
import java.util.UUID;

public class AddressMapper {

    public static AddressResponseDTO transformToDto(Address address) {
        return AddressResponseDTO.builder()
                .id(address.getId().toString())
                .city(Optional.of(address.getCity()).orElse(null))
                .street(Optional.of(address.getStreet()).orElse(null))
                .state(Optional.of(address.getState()).orElse(null))
                .country(Optional.of(address.getCountry()).orElse(null))
                .zipCode(Optional.of(address.getZipCode()).orElse(null))
                .build();
    }

    public static Address transformToModel(AddressResponseDTO addressResponseDTO) {
        return Address.builder()
                .id(UUID.fromString(addressResponseDTO.getId()))
                .city(addressResponseDTO.getCity())
                .street(addressResponseDTO.getStreet())
                .state(addressResponseDTO.getState())
                .country(addressResponseDTO.getCountry())
                .zipCode(addressResponseDTO.getZipCode())
                .build();
    }
}
