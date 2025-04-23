package com.pm.accountservice.controller;

import com.pm.accountservice.dto.address.AddressRequestDTO;
import com.pm.accountservice.dto.address.AddressResponseDTO;
import com.pm.accountservice.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/accounts/address")
@Tag(name = "Address", description = "API for managing Addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

//    @Operation(summary = "Get all addresses")
//    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public Flux<ResponseEntity<AddressResponseDTO>> getAllAddresses() {
//        var allAddresses = addressService.getAllAddresses();
//
//        return ResponseEntity.ok().body(allAddresses);
//    }
//
//    @Operation(summary = "Get all addresses")
//    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public Mono<ResponseEntity<AddressResponseDTO>> getAddressById(@PathVariable UUID id) {
//        var allAddresses = addressService.getAddressById(id);
//
//        return ResponseEntity.ok().body(allAddresses);
//    }
//
//    @Operation(summary = "Create a new address")
//    @PostMapping("/create")
//    @PreAuthorize("hasRole('ADMIN')")
//    public Mono<ResponseEntity<AddressResponseDTO>> createAddress(@Validated @RequestBody AddressRequestDTO addressRequestDTO) {
//        var addressCreated = addressService.createAddress(addressRequestDTO);
//
//        return ResponseEntity.ok().body(addressCreated);
//    }
}
