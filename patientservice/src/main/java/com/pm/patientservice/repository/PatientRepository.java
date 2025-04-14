package com.pm.patientservice.repository;

import com.pm.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByUserId(UUID userId);
    boolean existsByUserIdAndIdNot(UUID userId, UUID id);

    @Query("SELECT p FROM Patient p WHERE p.userId = :userId")
    Optional<Patient> findByUserId(@Param("userId") UUID userId);
}
