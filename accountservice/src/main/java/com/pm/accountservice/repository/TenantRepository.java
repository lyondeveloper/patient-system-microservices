package com.pm.accountservice.repository;

import com.pm.accountservice.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    boolean existsByEmail(String email);
    boolean existsByName(String name);
    Optional<Tenant> findByIdAndUsers_Id(UUID tenantId, UUID userId);
}
