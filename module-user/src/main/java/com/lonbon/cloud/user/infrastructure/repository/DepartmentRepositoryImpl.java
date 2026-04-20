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

/**
 * 部门仓库实现
 * <p>
 * 继承自 {@link EasyQueryRepository}，提供部门数据的持久化操作。
 * </p>
 *
 * @author lonbon
 * @since 1.0.0
 * @see EasyQueryRepository
 * @see DepartmentRepository
 */
@Repository
public class DepartmentRepositoryImpl extends EasyQueryRepository<Department, DepartmentProxy, DepartmentProxy.DepartmentProxyFetcher>
        implements DepartmentRepository {

    /**
     * 导航属性映射
     */
    private static final Map<String, SQLActionExpression2<IncludeContext, DepartmentProxy>> navigateMap = new HashMap<>();

    /**
     * 构造部门仓库
     *
     * @param easyEntityQuery EasyQuery实体查询客户端
     */
    public DepartmentRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, Department.class, proxy -> proxy.FETCHER);
    }

    /**
     * 获取导航属性映射
     *
     * @return 导航属性映射
     */
    @Override
    protected Map<String, SQLActionExpression2<IncludeContext, DepartmentProxy>> getNavigateMap() {
        return navigateMap;
    }

}
