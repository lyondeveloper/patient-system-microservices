package com.pm.accountservice.repository;

import com.pm.accountservice.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    boolean existsById(UUID id);
}
