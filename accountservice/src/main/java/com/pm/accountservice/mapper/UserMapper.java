package com.pm.accountservice.mapper;

import com.pm.accountservice.dto.user.UserResponseDTO;
import com.pm.accountservice.model.User;

public class UserMapper {

    public static UserResponseDTO toUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .isActive(user.isActive())
                .role(String.valueOf(user.getRole()))
                .build();
    }
}
