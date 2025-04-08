package com.pm.accountservice.service;

import com.pm.accountservice.dto.address.AddressResponseDTO;
import com.pm.accountservice.exceptions.address.AddressExceptions;
import com.pm.accountservice.mapper.AddressMapper;
import com.pm.accountservice.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AddressService {
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public AddressResponseDTO getAddressById(String id) {
        var address = addressRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new AddressExceptions("This address does not exist"));

        return AddressMapper.transformToDto(address);
    }
}
