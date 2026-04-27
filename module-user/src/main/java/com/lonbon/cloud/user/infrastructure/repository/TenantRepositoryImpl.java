package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.entity.proxy.TenantProxy;
import com.lonbon.cloud.user.domain.repository.TenantRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * 租户仓库实现
 * <p>
 * 继承自 {@link EasyQueryRepository}，提供租户数据的持久化操作。
 * </p>
 *
 * @author lonbon
 * @see EasyQueryRepository
 * @see TenantRepository
 * @since 1.0.0
 */
@Repository
public class TenantRepositoryImpl extends EasyQueryRepository<Tenant, TenantProxy, TenantProxy.TenantProxyFetcher>
        implements TenantRepository {

    private static final Map<String, SQLActionExpression2<IncludeContext, TenantProxy>> navigateMap = new HashMap<>();

    static {
        navigateMap.put(Tenant.Fields.ancestors, (c, p) -> {
            c.query(p.ancestors());
        });
        navigateMap.put(Tenant.Fields.descendants, (c, p) -> {
            c.query(p.descendants());
        });
        navigateMap.put(Tenant.Fields.attributes, (c, p) -> {
            c.query(p.attributes());
        });
    }

    /**
     * 构造租户仓库
     *
     * @param easyEntityQuery EasyQuery实体查询客户端
     */
    public TenantRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, Tenant.class, proxy -> proxy.FETCHER);
    }

    @Override
    protected Map<String, SQLActionExpression2<IncludeContext, TenantProxy>> getNavigateMap() {
        return navigateMap;
    }
}
