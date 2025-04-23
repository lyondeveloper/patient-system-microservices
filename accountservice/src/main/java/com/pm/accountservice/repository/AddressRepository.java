package com.pm.accountservice.repository;

import com.pm.accountservice.model.Address;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AddressRepository extends ReactiveCrudRepository<Address, Long> {

    @Query("SELECT a.* FROM address a WHERE a.tenant_id = :tenantId AND a.id = :id")
    Mono<Address> findByIdAndTenantId(Long id, Long tenantId);

    @Query("SELECT a.* FROM address a WHERE a.tenant_id = :tenantId")
    Flux<Address> findAllByTenantId(Long tenantId);

    // Method to return if an address alredy exists by the data provided
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END " +
    "FROM Address a where a.street = :street " +
    "AND a.city = :city " +
    "AND a.country = :country " +
    "AND a.state = :state " +
    "AND a.zip_code = :zipCode")
    Mono<Boolean> existsDuplicateAddress(
            @Param("street") String street,
            @Param("city") String city,
            @Param("country") String country,
            @Param("state") String state,
            @Param("zipCode") String zipCode
    );
}
