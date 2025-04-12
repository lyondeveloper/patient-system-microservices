package com.pm.accountservice.service;

import com.pm.accountservice.dto.address.AddressRequestDTO;
import com.pm.accountservice.dto.address.AddressResponseDTO;
import com.pm.accountservice.exceptions.address.AddressExceptions;
import com.pm.accountservice.mapper.AddressMapper;
import com.pm.accountservice.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AddressService {
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<AddressResponseDTO> getAllAddresses() {
        return addressRepository.findAll()
                .stream()
                .map(AddressMapper::transformToDto)
                .toList();
    }

    public AddressResponseDTO getAddressById(UUID id) {
        var address = addressRepository.findById(id)
                .orElseThrow(() -> new AddressExceptions("This address does not exist"));

        return AddressMapper.transformToDto(address);
    }

    public AddressResponseDTO createAddress(AddressRequestDTO addressRequestDTO) {
        var payloadMapped = AddressMapper.transformToModel(addressRequestDTO);

        if (addressRepository.existsDuplicateAddress(
                payloadMapped.getStreet(),
                payloadMapped.getCity(),
                payloadMapped.getCountry(),
                payloadMapped.getState(),
                payloadMapped.getZipCode()
        )) {
            throw new AddressExceptions("This address already exists");
        }

        return AddressMapper.transformToDto(addressRepository.save(payloadMapped));
    }
}
