package com.lonbon.cloud.base.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.api.proxy.entity.select.EntityQueryable;
import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.proxy.AbstractProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.easy.query.core.proxy.fetcher.AbstractFetcher;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public abstract class EasyQueryRepository<T extends ProxyEntityAvailable<T, TProxy>,
        TProxy extends AbstractProxyEntity<TProxy, T>, TChain extends AbstractFetcher<TProxy, T, TChain>>
        implements Repository<T, TProxy> {


    protected final EasyEntityQuery easyEntityQuery;

    protected final Class<T> entityType;

    protected final FetcherProvider<TProxy, T, TChain> fetcherProvider;

    public EasyQueryRepository(
            EasyEntityQuery easyEntityQuery, Class<T> entityType, FetcherProvider<TProxy, T, TChain> fetcherProvider) {
        this.easyEntityQuery = easyEntityQuery;
        this.entityType = entityType;
        this.fetcherProvider = fetcherProvider;
    }

    protected Map<String, SQLActionExpression2<IncludeContext, TProxy>> getNavigateMap() {
        return null;
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

    @Override
    public EntityQueryable<TProxy, T> queryable() {
        return easyEntityQuery.queryable(entityType);
    }

    @Override
    public boolean existsById(UUID id) {
        return getById(id).isPresent();
    }

    @Override
    public Optional<T> getById(UUID id) {
        return getById(id, false);
    }

    @Override
    public Optional<T> getById(UUID id, boolean tracking) {
        return getById(id, (SQLActionExpression2<IncludeContext, TProxy>) null, tracking);
    }

    @Override
    public Optional<T> getById(UUID id, SQLActionExpression2<IncludeContext, TProxy> navigate, boolean tracking) {
        EntityQueryable<TProxy, T> queryable = queryable();
        if (navigate != null) {
            queryable = queryable.include2(navigate);
        }
        if (tracking) {
            queryable = queryable.asTracking();
        }
        return queryable.whereById(id).singleOptional();
    }

    public Optional<T> getById(UUID id, List<String> navigate, boolean tracking) {
        // 处理空值情况
        if (navigate == null || navigate.isEmpty()) {
            return getById(id, tracking);
        }

        Map<String, SQLActionExpression2<IncludeContext, TProxy>> navigateMap = getNavigateMap();
        if (navigateMap == null) {
            return getById(id, tracking);
        }

        // 使用 Set 去重，避免重复处理
        Set<String> uniqueNavigate = new HashSet<>(navigate);

        return getById(id, (c, p) -> {
            for (String key : uniqueNavigate) {
                SQLActionExpression2<IncludeContext, TProxy> exp = navigateMap.get(key);
                if (exp != null) {
                    exp.apply(c, p);
                }
            }
        }, tracking);
    }

    @Override
    public Optional<T> getSingle(SQLActionExpression1<TProxy> whereExpression) {
        return queryable().where(whereExpression).singleOptional();
    }

    @Override
    public Optional<T> getSingle(
            SQLActionExpression1<TProxy> whereExpression,
            SQLActionExpression2<IncludeContext, TProxy> navigate) {
        EntityQueryable<TProxy, T> queryable = queryable();
        if (navigate != null) {
            queryable = queryable.include2(navigate);
        }
        return queryable.where(whereExpression).singleOptional();
    }

    @Override
    public T getSingleNotNull(SQLActionExpression1<TProxy> whereExpression) {
        return queryable().where(whereExpression).singleNotNull();
    }

    @Override
    public List<T> getAllByIds(Collection<UUID> ids) {
        return queryable().whereByIds(ids).toList();
    }

    @Override
    public List<T> getAll() {
        return queryable().toList();
    }

    @Override
    public List<T> getAll(SQLActionExpression1<TProxy> whereExpression) {
        return getAll(true, whereExpression);
    }

    @Override
    public List<T> getAll(boolean condition, SQLActionExpression1<TProxy> whereExpression) {
        return queryable().where(condition, whereExpression).toList();
    }

    @Override
    public PageResult<T> getPagination(Object whereObject, @NotNull Pageable pageable) {
        EasyPageResult<T> result = queryable().whereObject(whereObject).orderByObject(pageable.hasSort(),
                                                                                      new EasyQuerySort(
                                                                                              pageable.getSortables()))
                                              .toPageResult(pageable.getPage(), pageable.getSize());
        return new PageResult<>(pageable, result.getTotal(), result.getData());
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
        Optional<T> entity = getById(id);
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
            Optional<T> entity = getById(id);
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
