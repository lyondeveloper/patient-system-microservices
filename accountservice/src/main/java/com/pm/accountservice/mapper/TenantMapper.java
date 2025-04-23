package com.pm.accountservice.mapper;

import com.pm.accountservice.dto.tenant.TenantRequestDTO;
import com.pm.accountservice.dto.tenant.TenantResponseDTO;
import com.pm.accountservice.model.Address;
import com.pm.accountservice.model.Tenant;

import java.util.*;

public class TenantMapper {

//    public static TenantResponseDTO toDto(Tenant tenant) {
//        return TenantResponseDTO.builder()
//                .id(String.valueOf(tenant.getId()))
//                .name(tenant.getName())
//                .email(tenant.getEmail())
//                .phoneNumber(tenant.getPhoneNumber())
//                .address(tenant.getAddressId())
//                .build();
//    }
//
//    public static Tenant toEntity(TenantRequestDTO tenantRequestDto) {
//        return Tenant.builder()
//                .name(tenantRequestDto.getName())
//                .email(tenantRequestDto.getEmail())
//                .phoneNumber(Optional.of(tenantRequestDto)
//                        .map(TenantRequestDTO::getPhoneNumber)
//                        .orElse(null))
//                .address(mapAddress(tenantRequestDto))
//                .build();
//    }
//
//    private static Address mapAddress(TenantRequestDTO tenantRequestDTO) {
//        // returning only addressId as reference
//        return Optional.ofNullable(tenantRequestDTO)
//                .map(TenantRequestDTO::getAddress)
//                .orElse(null);
//    }
}
