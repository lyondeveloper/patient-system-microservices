package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.PatientAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {
    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final PatientRepository patientRepository;

    // los servicios siempre retornan el tipo DTO que esperan el frontend
    // estos dtos tanto de ida y de regreso son claves para manejar datos relevantes al frontend o la respuesta json
    public Flux<PatientResponseDTO> getPatients() {
        return patientRepository.findAll()
                .map(PatientMapper::toDto);
    }

    public Mono<PatientResponseDTO> getPatientByIdAndUserId(String patientId, String userId) {
        return patientRepository.findByIdAndUserId(UUID.fromString(patientId), UUID.fromString(userId))
                .switchIfEmpty(Mono.error(() -> {
                    log.warn("Patient with id {} and userId {} not found", patientId, userId);
                    return new PatientNotFoundException("Patient not found with patientId: " + patientId + " and userId: " + userId);
                }))
                .map(PatientMapper::toDto);
    }


    // this will only be called from user service when a user is created and type is patient
    public Mono<Void> createPatient(PatientRequestDTO patientRequestDTO) {

        var userId = UUID.fromString(patientRequestDTO.getUserId());

        return patientRepository.existsByUserId(userId)
                .flatMap((exists) -> {
                    // returns error if patient already exists
                    if (exists) {
                        return Mono.error(new PatientAlreadyExistsException("A patient with this userId " + userId + " already exists"));
                    }

                    // continue the reactive chaining if not
                    var newPatient = Patient.builder()
                            .userId(userId)
                            .firstName(patientRequestDTO.getFirstName())
                            .lastName(patientRequestDTO.getLastName())
                            .build();

                    return patientRepository.save(newPatient)
                            .doOnNext(savedPatient -> log.info("Patient created successfully with id: {}", savedPatient.getId().toString()))
                            .then();
                });
    }

    public Mono<PatientResponseDTO> updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        var userId = UUID.fromString(patientRequestDTO.getUserId());

        return patientRepository.findByIdAndUserId(id, userId)
                .switchIfEmpty(Mono.error(() -> new PatientNotFoundException("Patient not found with id " + id + " and userId " + userId)))
                .flatMap(currentPatient -> {
                    var updatedData = PatientMapper.toModel(patientRequestDTO);
                    return patientRepository.save(updatedData)
                            .doOnNext(savedPatient -> log.info("Patient updated successfully with id: {}", savedPatient.getId().toString()))
                            .map(PatientMapper::toDto);
                });
    }

    public Mono<Void> deletePatient(UUID id, UUID userId) {
        return patientRepository.findByIdAndUserId(id, userId)
                .switchIfEmpty(Mono.error(() -> new PatientNotFoundException("Patient with id: " + id + " not found.")))
                .flatMap(patient -> patientRepository.delete(patient)
                        .doOnSuccess(deletedPatient -> log.info("Patient deleted successfully with id: {}", patient.getId().toString())));
    }
}
