package com.lonbon.cloud.user.application.service;

import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.service.ClosureEntityService;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.entity.TenantClosure;
import com.lonbon.cloud.user.domain.entity.proxy.TenantClosureProxy;
import com.lonbon.cloud.user.domain.entity.proxy.TenantProxy;
import com.lonbon.cloud.user.domain.repository.TenantClosureRepository;
import com.lonbon.cloud.user.domain.repository.TenantRepository;
import com.lonbon.cloud.user.domain.service.TenantService;
import io.github.linpeilie.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 租户服务实现类
 * <p>
 * 提供租户的增删改查及层级关系管理功能。
 * 租户层级关系通过闭包表（TenantClosure）实现，支持多级租户结构。
 * </p>
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TenantServiceImpl extends ClosureEntityService<Tenant, TenantProxy, TenantClosure, TenantClosureProxy>
        implements TenantService {
    public TenantServiceImpl(
            Converter converter, TenantRepository repository, TenantClosureRepository closureRepository) {
        super(converter, repository, closureRepository, Tenant.class);
    }

    @Override
    protected SQLActionExpression2<IncludeContext, TenantProxy> navigate() {
        return (c, t) -> c.query(t.ancestors());
    }

    @Override
    protected TenantClosure createClosure(UUID ancestorId, UUID descendantId, int distance) {
        return new TenantClosure(ancestorId, descendantId, distance);
    }
}