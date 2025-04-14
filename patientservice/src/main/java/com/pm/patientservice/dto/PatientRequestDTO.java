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
    private String userId;

    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth;

    // Putting a group to registeredDate, to only require it to save, not to update
    @NotBlank(groups=CreatePatientValidationGroup.class, message = "Registered Date is required")
    private String registeredDate;

    private BigDecimal weight;
    private BigDecimal height;
    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Blood Type is required")
    private String bloodType;

    @NotBlank(message = "An emergency contact is required")
    private String emergencyContactPhone;

    private List<String> allergies;
    private String medicalHistory;

    private String insuranceProvider;
    private String insuranceNumber;
}
