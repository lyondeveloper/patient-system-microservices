package com.pm.patientservice.repository;

import com.pm.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends ReactiveCrudRepository<Patient, UUID> {
    Mono<Boolean> existsByUserId(UUID userId);
    Mono<Boolean> existsByUserIdAndIdNot(UUID userId, UUID id);

    @Query("SELECT p FROM Patient p WHERE p.id = :id AND p.userId = :userId")
    Mono<Patient> findByIdAndUserId(UUID id, UUID userId);
}
