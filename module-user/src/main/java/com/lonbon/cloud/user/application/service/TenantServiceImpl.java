package com.lonbon.cloud.user.application.service;

import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.repository.TenantRepository;
import com.lonbon.cloud.user.domain.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public Tenant createTenant(Tenant tenant) {
        tenant.setCreatedAt(OffsetDateTime.now());
        tenant.setCreatedBy(UUID.randomUUID());
        tenant.setUpdatedAt(OffsetDateTime.now());
        tenant.setVersionId(0);
        tenant.setDeleted(false);
        return tenantRepository.save(tenant);
    }

    @Override
    public Tenant updateTenant(Tenant tenant) {
        tenant.setUpdatedAt(OffsetDateTime.now());
        tenant.setVersionId(tenant.getVersionId() + 1);
        return tenantRepository.save(tenant);
    }

    @Override
    public void deleteTenant(UUID id) {
        tenantRepository.delete(id);
    }

    @Override
    public Optional<Tenant> getTenantById(UUID id) {
        return tenantRepository.findById(id);
    }

    @Override
    public Optional<Tenant> getTenantByName(String name) {
        return tenantRepository.findByName(name);
    }

    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @Override
    public Optional<Tenant> getDefaultTenant() {
        return tenantRepository.findDefaultTenant();
    }

    @Override
    public void setDefaultTenant(UUID tenantId) {
        // 先将所有租户的isDefault设置为false
        List<Tenant> tenants = tenantRepository.findAll();
        for (Tenant tenant : tenants) {
            tenant.setDefault(tenant.getId().equals(tenantId));
            tenant.setUpdatedAt(OffsetDateTime.now());
            tenantRepository.save(tenant);
        }
    }
}
