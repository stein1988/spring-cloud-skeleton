package com.lonbon.cloud.base.service;

import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.proxy.AbstractProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.repository.Repository;
import io.github.linpeilie.Converter;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Transactional(rollbackFor = Exception.class)
public abstract class ClosureEntityService<T extends ProxyEntityAvailable<T, TProxy> & ClosureAvailable<U>,
        TProxy extends AbstractProxyEntity<TProxy, T>, U extends ProxyEntityAvailable<U, UProxy> & Closure,
        UProxy extends AbstractProxyEntity<UProxy, U>>
        extends SimpleEntityService<T, TProxy> implements Service<T, TProxy> {

    protected final Repository<U, UProxy> closureRepository;

//    protected final ClosureProcessor<T, TProxy, U> processor;

    public ClosureEntityService(
            Converter converter, Repository<T, TProxy> repository, Repository<U, UProxy> closureRepository,
            Class<T> entityType) {
        super(converter, repository, entityType);
        this.closureRepository = closureRepository;
//        this.processor = processor;
    }

    protected abstract SQLActionExpression2<IncludeContext, TProxy> navigate();

    protected abstract U createClosure(UUID ancestorId, UUID descendantId, int distance);

    @Override
    public T createEntity(Object createDto) {
//        // 保存租户基本信息
//        Tenant createdTenant = tenantRepository.insert(converter.convert(tenant, Tenant.class));
//
//        // 创建自身闭包关系（distance=0，表示节点自身）
//        tenantClosureRepository.insert(createdTenant.createSelfClosure());
//
//        // 处理租户层级关系
//        UUID ancestorId = tenant.getAncestorId();
//        if (ancestorId != null) {
//            // 校验祖先租户是否存在
//            Tenant ancestorTenant = tenantRepository.getById(ancestorId).orElseThrow(
//                    () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "上级租户不存在，ID: " + ancestorId));
//
//            // 创建与祖先的直接父子关系（distance=1，表示直接子节点）
//            tenantClosureRepository.insert(new TenantClosure(ancestorId, createdTenant.getId(), 1));
//        }
//
//        return createdTenant;


        T entity = converter.convert(createDto, entityType);
        T created = repository.insert(entity);

        UUID id = created.getId();

        List<U> closures = new ArrayList<>();
        closures.add(createClosure(id, id, 0));

        UUID parentId = created.getParentId();
        if (parentId != null) {
            T parent = repository.getById(parentId, navigate(), false)
                                 .orElseThrow(() -> new RuntimeException("parent id is null"));

            closures.add(createClosure(parentId, id, 1));

            List<U> ancestors = parent.getAncestors();
            if (ancestors != null && !ancestors.isEmpty()) {
                for (U ancestor : ancestors) {
                    closures.add(createClosure(ancestor.getAncestorId(), id, ancestor.getDistance() + 1));
                }
            }


        }

        for (U closure : closures) {
            closureRepository.insert(closure);
        }

        return entity;
    }
}
