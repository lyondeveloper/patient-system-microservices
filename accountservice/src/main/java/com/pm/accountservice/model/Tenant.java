package com.pm.accountservice.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tenants")
public class Tenant extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name="phone_number", nullable = false)
    private String phoneNumber;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false, name="created_at")
    private Date createdAt;

    @Column(unique = true, nullable = false, name="updated_at")
    private Date updatedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="tenant_id",nullable = false)
    private List<TenantMedicament> medicaments;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TenantMedicament> getMedicaments() {
        return medicaments;
    }

    public void setMedicaments(List<TenantMedicament> medicaments) {
        this.medicaments = medicaments;
    }
}
