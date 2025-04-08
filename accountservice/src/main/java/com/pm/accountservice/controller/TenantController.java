package com.pm.accountservice.controller;

import com.pm.accountservice.dto.tenant.TenantRequestDTO;
import com.pm.accountservice.dto.tenant.TenantResponseDTO;
import com.pm.accountservice.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts/tenants")
@Tag(name = "Tenant", description = "API for managing tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Operation(summary = "Get all tenants")
    @GetMapping
    public ResponseEntity<List<TenantResponseDTO>> getAllTenants() {
        var allTenants = tenantService.getAllTenants();

        return ResponseEntity.ok().body(allTenants);
    }

    // TODO: validate correctly with exceptions
    @Operation(summary = "Creates a new tenant")
    @PostMapping("/create")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantResponseDTO> createTenant(@Validated({ Default.class }) @RequestBody TenantRequestDTO tenantRequestDTO) {
        var tenantCreated = tenantService.createTenant(tenantRequestDTO);

        return ResponseEntity.ok().body(tenantCreated);
    }
}
