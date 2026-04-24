package com.lonbon.cloud.user.application.service;

import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.expression.lambda.SQLFuncExpression1;
import com.easy.query.core.proxy.SQLSelectExpression;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.repository.Repository;
import com.lonbon.cloud.base.service.ClosureOperations;
import com.lonbon.cloud.base.service.EntityService;
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

@Service
@Transactional(rollbackFor = Exception.class)
public class TenantServiceImpl extends EntityService<Tenant, TenantProxy>
        implements TenantService, ClosureOperations<Tenant, TenantProxy, TenantClosure, TenantClosureProxy> {

    private final TenantClosureRepository closureRepository;

    public TenantServiceImpl(
            Converter converter, TenantRepository repository, TenantClosureRepository closureRepository) {
        super(converter, repository, Tenant.class);
        this.closureRepository = closureRepository;
    }

    @Override
    public Repository<TenantClosure, TenantClosureProxy> getClosureRepository() {
        return closureRepository;
    }

    @Override
    public TenantClosure createClosure(UUID ancestorId, UUID descendantId, Integer distance) {
        return new TenantClosure(ancestorId, descendantId, distance);
    }

    @Override
    public Tenant createBaseEntity(Object createDto) {
        return super.createEntity(createDto);
    }

    @Override
    public SQLActionExpression2<IncludeContext, TenantProxy> navigate() {
        return (c, t) -> c.query(t.ancestors());
    }

    @Override
    public SQLFuncExpression1<TenantProxy, SQLSelectExpression> setColumnParentId() {
        return TenantProxy::parentId;
    }
}