package com.lonbon.cloud.user.domain.repository;

import com.lonbon.cloud.user.domain.entity.Tenant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository {
    Tenant save(Tenant tenant);
    void delete(UUID id);
    Optional<Tenant> findById(UUID id);
    Optional<Tenant> findByName(String name);
    List<Tenant> findAll();
    Optional<Tenant> findDefaultTenant();
}
