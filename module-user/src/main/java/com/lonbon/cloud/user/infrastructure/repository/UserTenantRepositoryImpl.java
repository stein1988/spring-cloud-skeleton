package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.lonbon.cloud.user.domain.entity.UserTenant;
import com.lonbon.cloud.user.domain.entity.proxy.UserTenantProxy;
import com.lonbon.cloud.user.domain.repository.UserTenantRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserTenantRepositoryImpl
        extends EasyQueryRepository<UserTenant, UserTenantProxy, UserTenantProxy.UserTenantProxyFetcher>
        implements UserTenantRepository {
    public UserTenantRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, UserTenant.class, proxy -> proxy.FETCHER);
    }
}
