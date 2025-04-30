package com.pm.patientservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table(name = "patients")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends BaseModel {
    @NotNull
    @Column("user_id")
    private Long userId;

    @NotNull
    @Column("first_name")
    private String firstName;
    @NotNull
    @Column("last_name")
    private String lastName;

    @Column("blood_type")
    private String bloodType;

    private String gender;
    private BigDecimal weight;
    private BigDecimal height;
    // note: convert this to another model
    // maybe to take better the history if there are more fields
    // i have an idea talking with glay, but leave it like that
    @Column("medical_history")
    private String medicalHistory;

    // se necesita guardar la lista de strings en una tabla aparte para escabilidad
    // se pueden agregar muchas alergias y es preferible
    // JPA no permite hacerlo sin convertirlo a un String deserializado
    // o hacer este approach con ElementCollection
//    @ElementCollection
//    @CollectionTable(name = "patient_allergies", joinColumns = @JoinColumn(name = "patient_id"))
//    private List<String> allergies;

    @Column("emergency_contact_phone")
    private String emergencyContactPhone;

    @Column("insurance_provider")
    private String insuranceProvider;
    @Column("insurance_number")
    private String insuranceNumber;
}
