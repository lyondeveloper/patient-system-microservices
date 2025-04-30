package com.pm.patientservice.repository;

import com.pm.patientservice.model.Patient;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PatientRepository extends ReactiveCrudRepository<Patient, Long> {
    Mono<Patient> findByUserId(Long userId);
}
