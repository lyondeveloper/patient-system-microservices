package com.pm.accountservice.repository;

import com.pm.accountservice.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByEmail(String email);
    @Query("SELECT u.* FROM users u WHERE u.email = :email AND u.tenant_id = :tenantId")
    Mono<User> findByEmailAndTenantId(
            @Param("email") String email,
            @Param("tenantId") Long tenantId
    );

    @Query("SELECT u.*, a.* FROM users u LEFT JOIN address a ON u.address_id = a.id WHERE U.tenant_id = :tenantId")
    Flux<User> findAllWithRelationsByTenantId(Long tenantId);

    @Query("SELECT u.*, a.* FROM users u LEFT JOIN address a ON u.address_id = a.id WHERE u.id = :userId AND u.tenant_id = :tenantId")
    Mono<User> findByIdAndTenantIdWithRelations(
            @Param("userId") Long userId,
            @Param("tenantId") Long tenantId);

    Mono<Boolean> existsByEmailAndTenantId(String email, Long tenantId);
    Mono<Boolean> existsByIdAndTenantId(Long id, Long tenantId);
}

