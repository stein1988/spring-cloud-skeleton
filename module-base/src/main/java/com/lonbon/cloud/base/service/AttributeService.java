package com.lonbon.cloud.base.service;

import com.easy.query.core.proxy.ProxyEntity;

public interface AttributeService<T, TProxy extends ProxyEntity<TProxy, T>, A, AProxy extends ProxyEntity<AProxy, A>>
        extends AttributeOperation<T, TProxy, A, AProxy> {

    AttributeOperation<T, TProxy, A, AProxy> getAttributeOperation();

}
