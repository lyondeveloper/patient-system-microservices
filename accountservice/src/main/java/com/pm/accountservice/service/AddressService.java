package com.pm.accountservice.service;

import com.pm.accountservice.dto.address.AddressRequestDTO;
import com.pm.accountservice.dto.address.AddressResponseDTO;
import com.pm.accountservice.exceptions.address.AddressExceptions;
import com.pm.accountservice.exceptions.tenants.TenantNotFoundException;
import com.pm.accountservice.exceptions.users.UserProcessingException;
import com.pm.accountservice.mapper.AddressMapper;
import com.pm.accountservice.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final CommonService commonService;

    public Mono<AddressResponseDTO> getAddressById(Long id) {
        return addressRepository.findById(id)
                .map(AddressMapper::transformToDto)
                .switchIfEmpty(Mono.error(() -> new AddressExceptions("Address not found")))
                .doOnError((ex) -> log.error("Address not found with id {}: {}", id, ex.getMessage()));
    }

    public Mono<AddressResponseDTO> createAddress(AddressRequestDTO addressRequestDTO) {
        return commonService.getCurrentTenantId()
                .flatMap(tenantId -> {
                    var payloadMapped = AddressMapper.transformToModel(addressRequestDTO);

                    return addressRepository.existsDuplicateAddress(
                            payloadMapped.getStreet(),
                            payloadMapped.getCity(),
                            payloadMapped.getCountry(),
                            payloadMapped.getState(),
                            payloadMapped.getZipCode()
                    ).flatMap(exists -> {
                        if (exists) {
                            log.error("Address already exists");
                            return Mono.error(new AddressExceptions("Address already exists"));
                        }

                        return addressRepository.save(payloadMapped)
                                .map(AddressMapper::transformToDto)
                                .onErrorMap(DataAccessException.class, ex -> {
                                    log.error("DB error: {}", ex.getMessage());
                                    return new AddressExceptions("Unexpected database error");
                                })
                                .onErrorMap(RuntimeException.class, ex -> {
                                    log.error("Unexpected runtime error: {}", ex.getMessage());
                                    return new AddressExceptions("Unexpected runtime error");
                                });
                    }).onErrorResume((ex) -> {
                        log.error("Unexpected error while fetching existsDuplicateAddress: {}", ex.getMessage());
                        return Mono.error(new AddressExceptions("Unexpected Error"));
                    });
                })
                .onErrorResume((ex) -> {
                    log.error("Error on retrieving tenantId: {}", ex.getMessage());
                    return Mono.error(new TenantNotFoundException("No tenant available in security context"));
                });
    }
}
