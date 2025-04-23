package com.pm.patientservice.mapper;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.model.Patient;

import java.time.LocalDate;
import java.util.Optional;

public class PatientMapper {
    public static PatientResponseDTO toDto(Patient patient) {
        return PatientResponseDTO.builder()
                .id(patient.getId().toString())
                .userId(patient.getUserId().toString())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .createdAt(LocalDate.from(patient.getCreatedDate()))
                .lastModifiedDate(LocalDate.from(patient.getLastModifiedDate()))
                .build();
    }

    public static Patient toModel(PatientRequestDTO patientRequestDTO) {
        return Patient.builder()
                .weight(Optional.ofNullable(patientRequestDTO)
                        .map(PatientRequestDTO::getWeight)
                        .orElse(null))
                .height(Optional.ofNullable(patientRequestDTO)
                        .map(PatientRequestDTO::getHeight)
                        .orElse(null))
                .gender(Optional.ofNullable(patientRequestDTO)
                        .map(PatientRequestDTO::getGender)
                        .orElse(null))
                .bloodType(Optional.ofNullable(patientRequestDTO)
                        .map(PatientRequestDTO::getBloodType)
                        .orElse(null))
                .emergencyContactPhone(Optional.ofNullable(patientRequestDTO)
                        .map(PatientRequestDTO::getEmergencyContactPhone)
                        .orElse(null))
                .medicalHistory(Optional.ofNullable(patientRequestDTO)
                        .map(PatientRequestDTO::getMedicalHistory)
                        .orElse(null))
                .insuranceNumber(Optional.ofNullable(patientRequestDTO)
                        .map(PatientRequestDTO::getInsuranceNumber)
                        .orElse(null))
                .insuranceProvider(Optional.ofNullable(patientRequestDTO)
                        .map(PatientRequestDTO::getInsuranceProvider)
                        .orElse(null))
                .allergies(Optional.ofNullable(patientRequestDTO)
                        .map(PatientRequestDTO::getAllergies)
                        .orElse(null))
                .build();
    }
}
