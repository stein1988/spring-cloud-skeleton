package com.lonbon.cloud.user.domain.repository;

import com.lonbon.cloud.base.repository.Repository;
import com.lonbon.cloud.user.domain.entity.UserTenant;
import com.lonbon.cloud.user.domain.entity.proxy.UserTenantProxy;

public interface UserTenantRepository extends Repository<UserTenantProxy, UserTenant> {
    
}
