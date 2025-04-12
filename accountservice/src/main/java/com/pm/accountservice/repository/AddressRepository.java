package com.pm.accountservice.repository;

import com.pm.accountservice.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    Address findByIdAndTenantId(UUID id, UUID tenantId);
    boolean existsById(UUID id);

    // Method to return if an address alredy exists by the data provided
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END " +
    "FROM Address a where a.street = :street " +
    "AND a.city = :city " +
    "AND a.country = :country " +
    "AND a.state = :state " +
    "AND a.zipCode = :zipCode")
    boolean existsDuplicateAddress(
            @Param("street") String street,
            @Param("city") String city,
            @Param("country") String country,
            @Param("state") String state,
            @Param("zipCode") String zipCode
    );
}
