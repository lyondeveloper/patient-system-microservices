package com.pm.accountservice.service;

import com.pm.accountservice.dto.tenant.TenantRequestDTO;
import com.pm.accountservice.dto.tenant.TenantResponseDTO;
import com.pm.accountservice.exceptions.EmailAlreadyExistsException;
import com.pm.accountservice.exceptions.TenantNameAlreadyExistsException;
import com.pm.accountservice.mapper.TenantMapper;
import com.pm.accountservice.model.Tenant;
import com.pm.accountservice.repository.TenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public List<TenantResponseDTO> getAllTenants() {
        var allTenants = tenantRepository.findAll();

        return allTenants.stream().map(TenantMapper::toDto).toList();
    }

    public TenantResponseDTO createTenant(TenantRequestDTO tenantRequestDTO) {
        if (tenantRepository.existsByEmail(tenantRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("A Tenant with this email already exists");
        }

        if (tenantRepository.existsByName(tenantRequestDTO.getName())) {
            throw new TenantNameAlreadyExistsException("A tenant with this name already exists");
        }

        var newTenant = tenantRepository.save(TenantMapper.toEntity(tenantRequestDTO));

        return TenantMapper.toDto(newTenant);
    }
}
