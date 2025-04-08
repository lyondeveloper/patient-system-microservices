package com.pm.accountservice.mapper;

import com.pm.accountservice.dto.tenant.TenantRequestDTO;
import com.pm.accountservice.dto.tenant.TenantResponseDTO;
import com.pm.accountservice.model.Address;
import com.pm.accountservice.model.Tenant;
import com.pm.accountservice.model.TenantMedicament;
import com.pm.accountservice.model.User;

import java.util.*;

public class TenantMapper {

    public static TenantResponseDTO toDto(Tenant tenant) {
        return TenantResponseDTO.builder()
                .id(UUID.fromString(tenant.getId().toString()))
                .name(tenant.getName())
                .email(tenant.getEmail())
                .phoneNumber(tenant.getPhoneNumber())
                .address(tenant.getAddress())
                .medicaments(tenant.getMedicaments())
                .users(tenant.getUsers())
                .build();
    }

    public static Tenant toEntity(TenantRequestDTO tenantRequestDto) {
        return Tenant.builder()
                .name(tenantRequestDto.getName())
                .email(tenantRequestDto.getEmail())
                .phoneNumber(Optional.of(tenantRequestDto)
                        .map(TenantRequestDTO::getPhoneNumber)
                        .orElse(null))
                .address(mapAddress(tenantRequestDto))
                .medicaments(mapMedicaments(tenantRequestDto))
                .users(mapUsers(tenantRequestDto))
                .build();
    }

    private static List<User> mapUsers(TenantRequestDTO tenantRequestDto) {
        return Optional.ofNullable(tenantRequestDto)
                .map(TenantRequestDTO::getUsers)
                .orElse(Collections.emptyList());
    }

    private static List<TenantMedicament> mapMedicaments(TenantRequestDTO tenantRequestDTO) {
        return Optional.of(tenantRequestDTO)
                .map(TenantRequestDTO::getMedicaments)
                .orElse(Collections.emptyList());
    }

    private static Address mapAddress(TenantRequestDTO tenantRequestDTO) {
        // returning only addressId as reference
        return Optional.ofNullable(tenantRequestDTO)
                .map(TenantRequestDTO::getAddress)
                .orElse(null);
    }
}
