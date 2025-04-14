package com.pm.patientservice.mapper;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.model.Patient;

import java.time.LocalDate;
import java.util.UUID;

public class PatientMapper {
    public static PatientResponseDTO toDto(Patient patient) {
        return PatientResponseDTO.builder()
                .id(patient.getId().toString())
                .userId(patient.getUserId().toString())
                .createdAt(patient.getCreatedDate())
                .lastModifiedDate(patient.getLastModifiedDate())
                .build();
    }

    public static Patient toModel(PatientRequestDTO patientRequestDTO) {
        return Patient.builder()
                .userId(UUID.fromString(patientRequestDTO.getUserId()))
                .bloodType(patientRequestDTO.getBloodType())
                .gender(patientRequestDTO.getGender())
                .weight(patientRequestDTO.getWeight())
                .height(patientRequestDTO.getHeight())
                .dateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()))
                .registeredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate()))
                .medicalHistory(patientRequestDTO.getMedicalHistory())
                .allergies(patientRequestDTO.getAllergies())
                .emergencyContactPhone(patientRequestDTO.getEmergencyContactPhone())
                .insuranceNumber(patientRequestDTO.getInsuranceNumber())
                .insuranceProvider(patientRequestDTO.getInsuranceProvider())
                .build();
    }
}
