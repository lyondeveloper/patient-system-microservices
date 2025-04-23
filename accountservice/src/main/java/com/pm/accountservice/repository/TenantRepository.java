package com.pm.accountservice.repository;

import com.pm.accountservice.model.Tenant;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TenantRepository extends ReactiveCrudRepository<Tenant, Long> {
}
