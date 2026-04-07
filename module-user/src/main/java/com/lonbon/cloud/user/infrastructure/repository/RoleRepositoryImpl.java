package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.lonbon.cloud.user.domain.entity.Role;
import com.lonbon.cloud.user.domain.entity.proxy.RoleProxy;
import com.lonbon.cloud.user.domain.repository.RoleRepository;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepositoryImpl extends EasyQueryRepository<RoleProxy, Role, RoleProxy.RoleProxyFetcher>
        implements RoleRepository {
    
    public RoleRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, Role.class, proxy -> proxy.FETCHER);
    }
}
