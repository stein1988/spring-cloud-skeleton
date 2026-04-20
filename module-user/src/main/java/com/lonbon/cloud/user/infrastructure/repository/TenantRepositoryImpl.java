package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.entity.proxy.TenantProxy;
import com.lonbon.cloud.user.domain.repository.TenantRepository;
import org.springframework.stereotype.Repository;

/**
 * 租户仓库实现
 * <p>
 * 继承自 {@link EasyQueryRepository}，提供租户数据的持久化操作。
 * </p>
 *
 * @author lonbon
 * @since 1.0.0
 * @see EasyQueryRepository
 * @see TenantRepository
 */
@Repository
public class TenantRepositoryImpl extends EasyQueryRepository<Tenant, TenantProxy, TenantProxy.TenantProxyFetcher>
        implements TenantRepository {

    /**
     * 构造租户仓库
     *
     * @param easyEntityQuery EasyQuery实体查询客户端
     */
    public TenantRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, Tenant.class, proxy -> proxy.FETCHER);
    }
}
