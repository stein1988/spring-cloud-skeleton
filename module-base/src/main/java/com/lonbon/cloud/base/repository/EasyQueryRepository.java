package com.lonbon.cloud.base.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.api.proxy.entity.select.EntityQueryable;
import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.easy.query.core.proxy.AbstractProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.easy.query.core.proxy.fetcher.AbstractFetcher;
import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class EasyQueryRepository<TProxy extends AbstractProxyEntity<TProxy, T>,
        T extends ProxyEntityAvailable<T, TProxy>, TChain extends AbstractFetcher<TProxy, T, TChain>>
        implements Repository<TProxy, T> {

    protected final EasyEntityQuery easyEntityQuery;

    protected final Class<T> entityType;

    protected final FetcherProvider<TProxy, T, TChain> fetcherProvider;

    public EasyQueryRepository(
            EasyEntityQuery easyEntityQuery, Class<T> entityType, FetcherProvider<TProxy, T, TChain> fetcherProvider) {
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
    public <S extends T> S update(S entity) {
        easyEntityQuery.updatable(entity).executeRows();
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

    private EntityQueryable<TProxy, T> queryable() {
        return easyEntityQuery.queryable(entityType);
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public Optional<T> findById(UUID id) {
        return findById(id, false);
    }

    @Override
    public Optional<T> findById(UUID id, boolean tracking) {
        EntityQueryable<TProxy, T> queryable = queryable();
        if (tracking) {
            queryable = queryable.asTracking();
        }
        return queryable.whereById(id).singleOptional();
    }

    @Override
    public List<T> findAllByIds(Collection<UUID> ids) {
        return queryable().whereByIds(ids).toList();
    }

    @Override
    public List<T> findAll() {
        return queryable().toList();
    }

    @Override
    public List<T> findAll(SQLActionExpression1<TProxy> whereExpression) {
        return findAll(true, whereExpression);
    }

    @Override
    public List<T> findAll(boolean condition, SQLActionExpression1<TProxy> whereExpression) {
        return queryable().where(condition, whereExpression).toList();
    }

    @Override
    public PageResult<T> findPagination(Object whereObject, @NotNull Pageable pageable) {
        EasyPageResult<T> result = queryable().whereObject(whereObject).orderByObject(pageable.hasSort(),
                                                                                      new EasyQuerySort(
                                                                                              pageable.getSortables()))
                                              .toPageResult(pageable.getPage(), pageable.getSize());
        return new PageResult<>(pageable, result.getTotal(), result.getData());
    }

    @Override
    public Optional<T> singleOptional(SQLActionExpression1<TProxy> whereExpression) {
        return queryable().where(whereExpression).singleOptional();
    }

    @Override
    public T singleNotNull(SQLActionExpression1<TProxy> whereExpression) {
        return queryable().where(whereExpression).singleNotNull();
    }

    @Override
    public long count() {
        return queryable().count();
    }

    @Override
    public void delete(T entity) {
        easyEntityQuery.deletable(entity).executeRows();
    }

    @Override
    public void deleteById(UUID id) {
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
    public void deleteAllById(Iterable<? extends UUID> ids) {
        for (UUID id : ids) {
            Optional<T> entity = findById(id);
            entity.ifPresent(this::delete);
        }
    }

    @Override
    public <R> R track(Supplier<R> supplier) {
        var trackManager = easyEntityQuery.getRuntimeContext().getTrackManager();
        try {
            trackManager.begin();
            return supplier.get();
        } finally {
            trackManager.release();
        }
    }

    @Override
    public void track(Runnable runnable) {
        var trackManager = easyEntityQuery.getRuntimeContext().getTrackManager();
        try {
            trackManager.begin();
            runnable.run();
        } finally {
            trackManager.release();
        }
    }
}
