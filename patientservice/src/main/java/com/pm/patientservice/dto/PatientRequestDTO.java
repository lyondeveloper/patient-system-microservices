package com.pm.patientservice.dto;

import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientRequestDTO {
    @NotBlank(groups=CreatePatientValidationGroup.class, message = "A user to attach this patient to is required")
    private Long userId;
    @NotBlank(groups=CreatePatientValidationGroup.class, message = "First name is required")
    private String firstName;
    @NotBlank(groups=CreatePatientValidationGroup.class, message = "Last name is required")
    private String lastName;

    private BigDecimal weight;
    private BigDecimal height;
    private String gender;
    private String bloodType;
    private String emergencyContactPhone;
    // private List<String> allergies;
    private String medicalHistory;
    private String insuranceProvider;
    private String insuranceNumber;
}
