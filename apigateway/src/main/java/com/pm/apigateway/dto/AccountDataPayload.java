package com.pm.apigateway.dto;

public record AccountDataPayload(
        String id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String createdDate,
        AddressDataPayload address
) {
}
