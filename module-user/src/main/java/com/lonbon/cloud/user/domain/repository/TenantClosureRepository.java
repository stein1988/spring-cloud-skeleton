package com.lonbon.cloud.user.domain.repository;

import com.lonbon.cloud.base.repository.Repository;
import com.lonbon.cloud.user.domain.entity.TenantClosure;
import com.lonbon.cloud.user.domain.entity.proxy.TenantClosureProxy;

import java.util.UUID;

public interface TenantClosureRepository extends Repository<TenantClosureProxy, TenantClosure> {

}
