package com.pm.accountservice.mapper;

import com.pm.accountservice.dto.user.UserRequestDTO;
import com.pm.accountservice.dto.user.UserResponseDTO;
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
                .tenant(user.getTenant())
                .role(String.valueOf(user.getRole()))
                .build();
    }

    public static User toUserModel(UserRequestDTO userRequestDTO) {
        System.out.println("ENTRO");
        return User.builder()
                .firstName(userRequestDTO.getFirstName())
                .lastName(userRequestDTO.getLastName())
                .email(userRequestDTO.getEmail())
                .password(userRequestDTO.getPassword())
                .phoneNumber(userRequestDTO.getPhoneNumber())
                .isActive(userRequestDTO.isActive())
                .tenant(
                        Optional.of(userRequestDTO)
                                .map(UserRequestDTO::getTenantId)
                                .orElse(null)
                )
                // .address(mapAddress(userRequestDTO.getAddressId()))
                .build();
    }
}
