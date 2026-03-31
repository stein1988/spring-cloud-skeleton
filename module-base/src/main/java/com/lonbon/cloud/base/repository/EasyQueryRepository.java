package com.lonbon.cloud.base.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.easy.query.core.proxy.AbstractProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.easy.query.core.proxy.fetcher.AbstractFetcher;
import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class EasyQueryRepository<
        TProxy extends AbstractProxyEntity<TProxy, T>,
        T extends ProxyEntityAvailable<T, TProxy>,
        TChain extends AbstractFetcher<TProxy, T, TChain>,
        ID> implements Repository<TProxy, T, ID> {

    protected EasyEntityQuery easyEntityQuery;

    protected final Class<T> entityType;

    @NotNull
    protected final FetcherProvider<TProxy, T, TChain> fetcherProvider;

    public EasyQueryRepository(EasyEntityQuery easyEntityQuery, Class<T> entityType, @NotNull FetcherProvider<TProxy, T, TChain> fetcherProvider) {
        this.easyEntityQuery = easyEntityQuery;
        this.entityType = entityType;
        this.fetcherProvider = fetcherProvider;
    }

    @Override
    public <S extends T> S insert(S entity) {
        easyEntityQuery.insertable(entity).executeRows();
        return entity;
    }

    @Override
    public <S extends T> S save(S entity) {
        easyEntityQuery.insertable(entity).onConflictThen(o -> fetcherProvider.apply(o).allFields()).executeRows();
        return entity;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        for (S entity : entities) {
            save(entity);
        }
        return entities;
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    @Override
    public Optional<T> findById(ID id) {
        return easyEntityQuery.queryable(entityType).whereById(id).singleOptional();
    }

    @Override
    public Iterable<T> findAllByIds(Collection<ID> ids) {
        return easyEntityQuery.queryable(entityType).whereByIds(ids).toList();
    }

    @Override
    public Iterable<T> findAll() {
        return easyEntityQuery.queryable(entityType).toList();
    }

    @Override
    public Iterable<T> findAll(SQLActionExpression1<TProxy> whereExpression) {
        return findAll(true, whereExpression);
    }

    @Override
    public Iterable<T> findAll(boolean condition, SQLActionExpression1<TProxy> whereExpression) {
        return easyEntityQuery.queryable(entityType).where(condition, whereExpression).toList();
    }

    @Override
    public PageResult<T> findPagination(Object whereObject, @NotNull Pageable pageable) {
        EasyPageResult<T> result = easyEntityQuery.queryable(entityType)
                .whereObject(whereObject)
                .orderByObject(pageable.hasSort(), new EasyQuerySort(pageable.getSortables()))
                .toPageResult(pageable.getPage(), pageable.getSize());
        return new PageResult<T>(pageable, result.getTotal(), result.getData());
    }

    @Override
    public Optional<T> singleOptional(SQLActionExpression1<TProxy> whereExpression) {
        return easyEntityQuery.queryable(entityType).where(whereExpression).singleOptional();
    }

    @Override
    public T singleNotNull(SQLActionExpression1<TProxy> whereExpression) {
        return easyEntityQuery.queryable(entityType).where(whereExpression).singleNotNull();
    }

    @Override
    public long count() {
        return easyEntityQuery.queryable(entityType).count();
    }

    @Override
    public void delete(T entity) {
        easyEntityQuery.deletable(entity).executeRows();
    }

    @Override
    public void deleteById(ID id) {
        Optional<T> entity = findById(id);
        if (entity.isPresent()) {
            delete(entity.get());
        } else {
            throw new IllegalArgumentException("Entity not found with id: " + id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }

    @Override
    public void deleteAll() {
        easyEntityQuery.deletable(entityType).executeRows();
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        for (ID id : ids) {
            Optional<T> entity = findById(id);
            entity.ifPresent(this::delete);
        }
    }
}
