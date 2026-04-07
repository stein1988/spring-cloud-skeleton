package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.lonbon.cloud.user.domain.entity.TenantClosure;
import com.lonbon.cloud.user.domain.entity.proxy.TenantClosureProxy;
import com.lonbon.cloud.user.domain.repository.TenantClosureRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TenantClosureRepositoryImpl
        extends EasyQueryRepository<TenantClosureProxy, TenantClosure, TenantClosureProxy.TenantClosureProxyFetcher>
        implements TenantClosureRepository {
    
    public TenantClosureRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, TenantClosure.class, proxy -> proxy.FETCHER);
    }
}
