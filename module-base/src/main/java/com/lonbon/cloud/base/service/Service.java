package com.lonbon.cloud.base.service;

import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.proxy.ProxyEntity;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public interface Service<T, TProxy extends ProxyEntity<TProxy, T>> {
    T createEntity(Object createDto);

    T updateEntity(UUID id, Object updateDto);

    T updateEntity(UUID id, Function<T, T> updateFunc);

    void deleteEntity(UUID id);

    Optional<T> getEntityById(UUID id);

    Optional<T> getEntityById(UUID id, SQLActionExpression2<IncludeContext, TProxy> navigate, boolean tracking);

    Optional<T> getEntityById(UUID id, List<String> navigate, boolean tracking);

    Optional<T> getEntity(SQLActionExpression1<TProxy> whereExpression);

    Optional<T> getEntity(
            SQLActionExpression1<TProxy> whereExpression, SQLActionExpression2<IncludeContext, TProxy> navigate);

    List<T> getAllEntities();

    PageResult<T> getPaginationEntities(Object whereObject, Pageable pageable);
}
