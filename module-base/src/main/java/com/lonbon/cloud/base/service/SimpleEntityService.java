package com.lonbon.cloud.base.service;

import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.proxy.AbstractProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;
import com.lonbon.cloud.base.exception.BusinessException;
import com.lonbon.cloud.base.exception.ErrorCode;
import com.lonbon.cloud.base.repository.Repository;
import io.github.linpeilie.Converter;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Transactional(rollbackFor = Exception.class)
public abstract class SimpleEntityService<T extends ProxyEntityAvailable<T, TProxy>,
        TProxy extends AbstractProxyEntity<TProxy, T>, TRepository extends Repository<T, TProxy>>
        implements Service<T, TProxy> {

    protected final Converter converter;

    protected final TRepository repository;

    protected final Class<T> entityType;
    
    public SimpleEntityService(Converter converter, TRepository repository, Class<T> entityType) {
        this.converter = converter;
        this.repository = repository;
        this.entityType = entityType;
    }

    @Override
    public T createEntity(Object createDto) {
        T entity = converter.convert(createDto, entityType);
        repository.insert(entity);
        return entity;
    }

    @Override
    public T updateEntity(UUID id, Object updateDto) {
        return this.updateEntity(id, entity -> converter.convert(updateDto, entity));
    }

    @Override
    public T updateEntity(UUID id, @NotNull Function<T, T> updateFunc) {
        return repository.track(() -> {
            T existing = repository.getById(id, true).orElseThrow(
                    () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Entity not found, ID: " + id));

            T updated = updateFunc.apply(existing);
            return repository.update(updated);
        });
    }


    @Override
    public void deleteEntity(UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Optional<T> getEntityById(UUID id) {
        return repository.getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Optional<T> getEntityById(UUID id, SQLActionExpression2<IncludeContext, TProxy> navigate, boolean tracking) {
        return repository.getById(id, navigate, tracking);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Optional<T> getEntityById(UUID id, List<String> navigate, boolean tracking) {
        return repository.getById(id, navigate, tracking);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Optional<T> getEntity(SQLActionExpression1<TProxy> whereExpression) {
        return repository.getSingle(whereExpression);
    }

    @Override
    public Optional<T> getEntity(
            SQLActionExpression1<TProxy> whereExpression,
            SQLActionExpression2<IncludeContext, TProxy> navigate) {
        return repository.getSingle(whereExpression, navigate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<T> getAllEntities() {
        return repository.getAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public PageResult<T> getPaginationEntities(Object whereObject, Pageable pageable) {
        return repository.getPagination(whereObject, pageable);
    }
}
