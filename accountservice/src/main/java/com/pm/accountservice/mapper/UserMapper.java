package com.pm.accountservice.mapper;

import com.pm.accountservice.dto.user.UserRequestDTO;
import com.pm.accountservice.dto.user.UserResponseDTO;
import com.pm.accountservice.model.User;
import com.pm.accountservice.util.UserTypes;

import java.time.LocalDate;
import java.util.Optional;

public class UserMapper {

    public static UserResponseDTO toUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .isActive(user.isActive())
                // address can be null, we wrap those in Optional and
                // build it with correspondant mappers methods
                .tenantId(user.getTenantId())
                .dateOfBirth(user.getDateOfBirth().toString())
                .type(String.valueOf(UserTypes.fromName(String.valueOf(user.getType()))))
//                .address(Optional.of(user)
//                        .map(User::getAddressId)
//                        .map(AddressMapper::transformToDto)
//                        .orElse(null))
                .role(String.valueOf(user.getRole()))
                .build();
    }

    public static User toUserModel(UserRequestDTO userRequestDTO) {
        return User.builder()
                .firstName(userRequestDTO.getFirstName())
                .lastName(userRequestDTO.getLastName())
                .dateOfBirth(LocalDate.parse(userRequestDTO.getDateOfBirth()))
                .email(userRequestDTO.getEmail())
                .password(userRequestDTO.getPassword())
                .phoneNumber(userRequestDTO.getPhoneNumber())
                .isActive(userRequestDTO.isActive())
                .tenantId(userRequestDTO.getTenantId())
                .type(UserTypes.fromName(userRequestDTO.getType()))
                .addressId(userRequestDTO.getAddressId())
                .build();
    }
}
