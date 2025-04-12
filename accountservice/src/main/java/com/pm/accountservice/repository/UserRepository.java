package com.pm.accountservice.repository;

import com.pm.accountservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndTenantId(
            @Param("email") String email,
            @Param("tenantId") UUID tenantId
    );
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.address")
    List<User> findAllWithRelationsByTenantId(UUID tenantId);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.address " +
            "WHERE u.id = :userId AND u.tenantId = :tenantId")
    Optional<User> findByIdAndTenantIdWithRelations(
            @Param("userId") UUID userId,
            @Param("tenantId") UUID tenantId);

    boolean existsByIdAndTenantId(UUID id, UUID tenantId);
}

