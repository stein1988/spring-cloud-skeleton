package com.lonbon.cloud.user.application.service;

import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.expression.lambda.SQLFuncExpression1;
import com.easy.query.core.proxy.SQLSelectExpression;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.repository.Repository;
import com.lonbon.cloud.base.service.ClosureExtension;
import com.lonbon.cloud.base.service.ClosureOperation;
import com.lonbon.cloud.base.service.EntityServiceImpl;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.entity.TenantClosure;
import com.lonbon.cloud.user.domain.entity.proxy.DepartmentProxy;
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
public class TenantServiceImpl extends EntityServiceImpl<Tenant, TenantProxy> implements TenantService {

    private final ClosureExtension<Tenant, TenantProxy, TenantClosure, TenantClosureProxy> closureExtension;

    public TenantServiceImpl(
            Converter converter, TenantRepository repository, TenantClosureRepository closureRepository) {
        super(converter, repository, Tenant.class);
        this.closureExtension = new ClosureExtension<>(repository, closureRepository, (c, t) -> c.query(t.ancestors()), TenantProxy::parentId) {
            @Override
            protected TenantClosure createClosure(UUID ancestorId, UUID descendantId, Integer distance) {
                return new TenantClosure(ancestorId, descendantId, distance);
            }

            @Override
            protected Tenant createBaseEntity(Object createDto) {
                return TenantServiceImpl.this.createEntity(createDto);
            }
        };
    }

    @Override
    public ClosureOperation<Tenant, TenantProxy, TenantClosure, TenantClosureProxy> getClosureOperation() {
        return closureExtension;
    }
}