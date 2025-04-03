package com.pm.accountservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tenant_medicaments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TenantMedicament extends BaseModel {
    @Id
    private UUID medicamentId;

    @Column(nullable = false)
    private String name;
}

