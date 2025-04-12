package com.pm.accountservice.mapper;

import com.pm.accountservice.dto.tenant.TenantResponseDTO;
import com.pm.accountservice.dto.user.UserRequestDTO;
import com.pm.accountservice.dto.user.UserResponseDTO;
import com.pm.accountservice.model.Address;
import com.pm.accountservice.model.User;

import java.util.*;

public class UserMapper {

    public static UserResponseDTO toUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .isActive(user.isActive())
                // address can be null, we wrap those in Optional and
                // build it with correspondant mappers methods
                .tenantId(user.getTenantId().toString())
                .address(Optional.of(user)
                        .map(User::getAddress)
                        .map(AddressMapper::transformToDto)
                        .orElse(null))
                .role(String.valueOf(user.getRole()))
                .build();
    }

    public static User toUserModel(UserRequestDTO userRequestDTO) {
        return User.builder()
                .firstName(userRequestDTO.getFirstName())
                .lastName(userRequestDTO.getLastName())
                .email(userRequestDTO.getEmail())
                .password(userRequestDTO.getPassword())
                .phoneNumber(userRequestDTO.getPhoneNumber())
                .isActive(userRequestDTO.isActive())
                .tenantId(userRequestDTO.getTenantId())
                .address(Optional.of(userRequestDTO)
                        .map(UserRequestDTO::getAddressId)
                        .map(addressId -> Address.builder()
                                .id(UUID.fromString(addressId))
                                .build())
                        .orElse(null))
                .build();
    }
}
