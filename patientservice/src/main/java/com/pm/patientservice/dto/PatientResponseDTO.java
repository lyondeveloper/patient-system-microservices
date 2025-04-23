package com.pm.patientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDTO {
    private String id;
    private String userId;
    private String gender;
    private String firstName;
    private String lastName;
    private List<String> allergies;
    private String medicalHistory;
    private String emergencyContactPhone;
    private String insuranceNumber;
    private BigDecimal height;
    private BigDecimal weight;
    private String bloodType;
    private String insuranceProvider;
    private LocalDate createdAt;
    private LocalDate lastModifiedDate;
}
