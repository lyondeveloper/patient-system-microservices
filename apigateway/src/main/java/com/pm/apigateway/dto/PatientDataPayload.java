package com.pm.apigateway.dto;

import java.math.BigDecimal;
import java.util.List;

public record PatientDataPayload(
        Long id,
        Long userId,
        String createdAt,
        String dateOfBirth,
        String registeredDate,
        String bloodType,
        String gender,
        BigDecimal weight,
        BigDecimal height,
        String medicalHistory,
        List<String> allergies,
        String emergencyContactPhone,
        String insuranceProvider,
        String insuranceNumber
) {
}
