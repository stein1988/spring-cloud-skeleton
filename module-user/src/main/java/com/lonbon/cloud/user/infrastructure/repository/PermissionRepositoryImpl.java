package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.lonbon.cloud.user.domain.entity.Permission;
import com.lonbon.cloud.user.domain.entity.proxy.PermissionProxy;
import com.lonbon.cloud.user.domain.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class PermissionRepositoryImpl extends EasyQueryRepository<PermissionProxy, Permission, PermissionProxy.PermissionProxyFetcher> implements PermissionRepository {
    @Autowired
    public PermissionRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, Permission.class, proxy -> proxy.FETCHER);
    }
}
