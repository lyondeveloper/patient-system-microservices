package com.pm.apigateway.dto;

public record AddressDataPayload(
        String id,
        String city,
        String street,
        String state,
        String country,
        String zipCode
) {
}
