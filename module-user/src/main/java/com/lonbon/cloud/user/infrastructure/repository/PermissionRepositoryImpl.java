package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.lonbon.cloud.user.domain.entity.Permission;
import com.lonbon.cloud.user.domain.entity.proxy.PermissionProxy;
import com.lonbon.cloud.user.domain.repository.PermissionRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionRepositoryImpl
        extends EasyQueryRepository<Permission, PermissionProxy, PermissionProxy.PermissionProxyFetcher>
        implements PermissionRepository {
    
    public PermissionRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, Permission.class, proxy -> proxy.FETCHER);
    }
}
