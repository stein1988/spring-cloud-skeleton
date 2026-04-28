package com.lonbon.cloud.base.service;

import com.easy.query.core.proxy.ProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.repository.Repository;

import java.util.List;

public abstract class AttributeExtension<T extends ProxyEntityAvailable<T, TProxy> & AttributeAvailable<A>,
        TProxy extends ProxyEntity<TProxy, T>, A extends AttributeEntity & ProxyEntityAvailable<A, AProxy>,
        AProxy extends ProxyEntity<AProxy, A>>
        implements AttributeOperation<T, TProxy, A, AProxy>, EntityServiceInterceptor<T> {

    /**
     * 主实体仓库
     */
    private final Repository<T, TProxy> entityRepository;

    /**
     * 属性实体仓库
     */
    private final Repository<A, AProxy> attributeRepository;

    public AttributeExtension(Repository<T, TProxy> entityRepository, Repository<A, AProxy> attributeRepository) {
        this.entityRepository = entityRepository;
        this.attributeRepository = attributeRepository;
    }

    @Override
    public void postCreate(T entity) {
        List<A> attributes = entity.getAttributes();
        if (attributes == null || attributes.isEmpty()) {
            return;
        }

        for (A attribute : attributes) {
            attribute.setEntityId(entity.getId());
        }
        attributeRepository.insert(attributes);
    }
}
