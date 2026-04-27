package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.lonbon.cloud.user.domain.entity.TenantAttribute;
import com.lonbon.cloud.user.domain.entity.proxy.TenantAttributeProxy;
import com.lonbon.cloud.user.domain.repository.TenantAttributeRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TenantAttributeRepositoryImpl extends
                                           EasyQueryRepository<TenantAttribute, TenantAttributeProxy,
                                                   TenantAttributeProxy.TenantAttributeProxyFetcher>
        implements TenantAttributeRepository {
    public TenantAttributeRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, TenantAttribute.class, proxy -> proxy.FETCHER);
    }
}
