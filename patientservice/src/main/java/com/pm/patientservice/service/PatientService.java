package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.PatientAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // los servicios siempre retornan el tipo DTO que esperan el frontend
    // estos dtos tanto de ida y de regreso son claves para manejar datos relevantes al frontend o la respuesta json
    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients
                .stream()
                .map(PatientMapper::toDto)
                .toList();
    }

    public PatientResponseDTO getPatientByUserId(String userId) {
        Patient patient = patientRepository.findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with userId: " + userId));
        return PatientMapper.toDto(patient);
    }

    public PatientResponseDTO getPatientById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        return PatientMapper.toDto(patient);
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByUserId(UUID.fromString(patientRequestDTO.getUserId()))) {
            throw new PatientAlreadyExistsException("A patient with this userId " + patientRequestDTO.getUserId() + " already exists");
        }

        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        return PatientMapper.toDto(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        // find by id or throw exception to handle errors
        Patient currentPatient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));

        if (patientRepository.existsByUserIdAndIdNot(UUID.fromString(patientRequestDTO.getUserId()), id)) {
            throw new PatientAlreadyExistsException("A patient with this userId " + patientRequestDTO.getUserId() + " already exists");
        }

        Patient updatedPatient = patientRepository.save(currentPatient);

        // always return data as a DTO
        return PatientMapper.toDto(updatedPatient);
    }

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }
}
