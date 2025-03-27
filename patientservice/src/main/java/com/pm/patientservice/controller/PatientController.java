package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import com.pm.patientservice.repository.PatientRepository;
import com.pm.patientservice.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient", description = "API for managing patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "Get Patients")
    public ResponseEntity<List<PatientResponseDTO>> getPatients() {
        List<PatientResponseDTO> patients = patientService.getPatients();

        return ResponseEntity.ok().body(patients);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single patient")
    public ResponseEntity<PatientResponseDTO> getSinglePatient(@PathVariable UUID id) {
        PatientResponseDTO patient = patientService.getSinglePatient(id);
        return ResponseEntity.ok().body(patient);
    }

    @PostMapping
    @Operation(summary = "Create a new patient")
    // validated aca con el grupo para que el controlador sepa que no requerimos que chequee la validacion para registeredDate notBlank ya que
    // no queremos que al actualizar un paciente, este campo vuelva a ser actualizado
    public ResponseEntity<PatientResponseDTO> createPatient(@Validated({ Default.class, CreatePatientValidationGroup.class }) @RequestBody PatientRequestDTO patientRequestDTO) {
        PatientResponseDTO newPatient = patientService.createPatient(patientRequestDTO);

        return ResponseEntity.ok().body(newPatient);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a patient")
    // path variable agarra el id o el parametro de la URL
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable UUID id, @Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO) {
        PatientResponseDTO updatedPatient = patientService.updatePatient(id, patientRequestDTO);
        return ResponseEntity.ok().body(updatedPatient);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}


