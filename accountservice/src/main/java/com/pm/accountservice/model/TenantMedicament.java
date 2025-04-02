package com.pm.accountservice.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tenant_medicaments")
public class TenantMedicament extends BaseModel {
    @Id
    private UUID medicamentId;

    @Column(nullable = false)
    private String name;

    public UUID getMedicamentId() {
        return medicamentId;
    }

    public void setMedicamentId(UUID id) {
        this.medicamentId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

