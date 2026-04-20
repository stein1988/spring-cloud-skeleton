package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.lonbon.cloud.user.domain.entity.DepartmentClosure;
import com.lonbon.cloud.user.domain.entity.proxy.DepartmentClosureProxy;
import com.lonbon.cloud.user.domain.repository.DepartmentClosureRepository;
import org.springframework.stereotype.Repository;

@Repository
public class DepartmentClosureRepositoryImpl
        extends EasyQueryRepository<DepartmentClosure, DepartmentClosureProxy, DepartmentClosureProxy.DepartmentClosureProxyFetcher>
        implements DepartmentClosureRepository {
    
    public DepartmentClosureRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, DepartmentClosure.class, proxy -> proxy.FETCHER);
    }
}
