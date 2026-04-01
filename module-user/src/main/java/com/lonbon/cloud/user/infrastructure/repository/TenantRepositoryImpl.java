package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.entity.proxy.TenantProxy;
import com.lonbon.cloud.user.domain.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class TenantRepositoryImpl extends EasyQueryRepository<TenantProxy, Tenant, TenantProxy.TenantProxyFetcher> implements TenantRepository {
    @Autowired
    public TenantRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, Tenant.class, proxy -> proxy.FETCHER);
    }
}
