package com.pm.patientservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "patients")
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "blood_type", nullable = false)
    private String bloodType;
    @Column(nullable = false)
    private String gender;

    private BigDecimal weight;
    private BigDecimal height;

    @Column(name = "registered_date", nullable = false)
    private LocalDate registeredDate;
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
    // note: convert this to another model
    // maybe to take better the history if there are more fields
    // i have an idea talking with glay, but leave it like that
    @Column(name = "medical_history")
    private String medicalHistory;

    // se necesita guardar la lista de strings en una tabla aparte para escabilidad
    // se pueden agregar muchas alergias y es preferible
    // JPA no permite hacerlo sin convertirlo a un String deserializado
    // o hacer este approach con ElementCollection
    @ElementCollection
    @CollectionTable(name = "patient_allergies", joinColumns = @JoinColumn(name = "patient_id"))
    private List<String> allergies;

    @Column(name = "emergency_contact_phone", nullable = false)
    private String emergencyContactPhone;

    @Column(name = "insurance_provider")
    private String insuranceProvider;
    @Column(name = "insurance_number")
    private String insuranceNumber;
}
