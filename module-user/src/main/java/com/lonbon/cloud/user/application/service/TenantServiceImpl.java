package com.lonbon.cloud.user.application.service;

import com.lonbon.cloud.base.service.*;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.entity.TenantAttribute;
import com.lonbon.cloud.user.domain.entity.TenantClosure;
import com.lonbon.cloud.user.domain.entity.proxy.TenantAttributeProxy;
import com.lonbon.cloud.user.domain.entity.proxy.TenantClosureProxy;
import com.lonbon.cloud.user.domain.entity.proxy.TenantProxy;
import com.lonbon.cloud.user.domain.repository.TenantAttributeRepository;
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

    private final AttributeExtension<Tenant, TenantProxy, TenantAttribute, TenantAttributeProxy> attributeExtension;

    public TenantServiceImpl(
            Converter converter, TenantRepository repository, TenantClosureRepository closureRepository,
            TenantAttributeRepository attributeRepository) {
        super(converter, repository, Tenant.class);
        this.closureExtension = new ClosureExtension<>(repository, closureRepository, (c, t) -> c.query(t.ancestors()),
                                                       TenantProxy::parentId) {
            @Override
            protected TenantClosure createClosure(UUID ancestorId, UUID descendantId, Integer distance) {
                return new TenantClosure(ancestorId, descendantId, distance);
            }
        };
        this.attributeExtension = new AttributeExtension<>(repository, attributeRepository) {
        };
    }

    @Override
    public ClosureOperation<Tenant, TenantProxy, TenantClosure, TenantClosureProxy> getClosureOperation() {
        return closureExtension;
    }

    @Override
    public AttributeOperation<Tenant, TenantProxy, TenantAttribute, TenantAttributeProxy> getAttributeOperation() {
        return attributeExtension;
    }
}