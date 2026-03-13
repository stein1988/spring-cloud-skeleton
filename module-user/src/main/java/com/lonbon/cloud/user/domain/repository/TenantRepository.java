package com.lonbon.cloud.user.domain.repository;

import com.lonbon.cloud.base.repository.Repository;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.entity.proxy.TenantProxy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends Repository<TenantProxy, Tenant, UUID> {

}
