package com.lonbon.cloud.user.domain.service;

import com.easy.query.core.api.pagination.EasyPageResult;
import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;
import com.lonbon.cloud.user.domain.dto.TenantCreateDTO;
import com.lonbon.cloud.user.domain.dto.TenantUpdateDTO;
import com.lonbon.cloud.user.domain.entity.Tenant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantService {
    Tenant createTenant(TenantCreateDTO tenant);
    Tenant updateTenant(UUID id, TenantUpdateDTO tenant);
    void deleteTenant(UUID id);
    Optional<Tenant> getTenantById(UUID id);
    Optional<Tenant> getTenantByName(String name);
    List<Tenant> getAllTenants();
    PageResult<Tenant> getTenants(Object whereObject, Pageable pageable);
}
