package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.lonbon.cloud.user.domain.entity.Department;
import com.lonbon.cloud.user.domain.entity.proxy.DepartmentProxy;
import com.lonbon.cloud.user.domain.repository.DepartmentRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class DepartmentRepositoryImpl extends EasyQueryRepository<Department, DepartmentProxy, DepartmentProxy.DepartmentProxyFetcher>
        implements DepartmentRepository {

    private static final Map<String, SQLActionExpression2<IncludeContext, DepartmentProxy>> navigateMap = new HashMap<>();

    public DepartmentRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, Department.class, proxy -> proxy.FETCHER);
    }

    @Override
    protected Map<String, SQLActionExpression2<IncludeContext, DepartmentProxy>> getNavigateMap() {
        return navigateMap;
    }

}
