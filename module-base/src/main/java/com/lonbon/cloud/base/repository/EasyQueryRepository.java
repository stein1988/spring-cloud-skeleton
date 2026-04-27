package com.lonbon.cloud.base.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.api.proxy.entity.select.EntityQueryable;
import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.expression.lambda.SQLFuncExpression1;
import com.easy.query.core.proxy.AbstractProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.easy.query.core.proxy.SQLSelectExpression;
import com.easy.query.core.proxy.fetcher.AbstractFetcher;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 * EasyQuery仓库实现基类
 * <p>
 * 提供了基于EasyQuery框架的仓库实现，支持类型安全的查询和导航属性处理。
 * 所有查询操作都通过{@link EasyEntityQuery}执行。
 * </p>
 *
 * @param <T>      实体类型
 * @param <TProxy> 实体代理类型
 * @param <TChain> 实体抓取器类型
 * @author lonbon
 * @since 1.0.0
 */
public abstract class EasyQueryRepository<T extends ProxyEntityAvailable<T, TProxy>,
        TProxy extends AbstractProxyEntity<TProxy, T>, TChain extends AbstractFetcher<TProxy, T, TChain>>
        implements Repository<T, TProxy> {


    /**
     * EasyQuery实体查询客户端
     */
    protected final EasyEntityQuery easyEntityQuery;

    /**
     * 实体类型Class
     */
    protected final Class<T> entityType;

    /**
     * 抓取器提供者
     */
    protected final FetcherProvider<TProxy, T, TChain> fetcherProvider;

    /**
     * 构造EasyQuery仓库
     *
     * @param easyEntityQuery 实体查询客户端
     * @param entityType      实体类型
     * @param fetcherProvider 抓取器提供者
     */
    public EasyQueryRepository(
            EasyEntityQuery easyEntityQuery, Class<T> entityType, FetcherProvider<TProxy, T, TChain> fetcherProvider) {
        this.easyEntityQuery = easyEntityQuery;
        this.entityType = entityType;
        this.fetcherProvider = fetcherProvider;
    }

    /**
     * 获取导航属性映射
     * <p>
     * 子类可重写此方法，提供导航属性名称到表达式的映射。
     * </p>
     *
     * @return 导航属性映射，未配置时返回null
     */
    protected @Nullable Map<String, SQLActionExpression2<IncludeContext, TProxy>> getNavigateMap() {
        return null;
    }

    /**
     * 处理导航属性列表，转换为导航属性表达式
     *
     * @param navigate 导航属性名称列表
     * @return 导航属性表达式，如果导航属性列表为空或导航映射为 null 则返回 null
     */
    protected @Nullable SQLActionExpression2<IncludeContext, TProxy> processNavigateList(List<String> navigate) {
        // 处理空值情况
        if (navigate.isEmpty()) {
            return null;
        }

        Map<String, SQLActionExpression2<IncludeContext, TProxy>> navigateMap = getNavigateMap();
        if (navigateMap == null) {
            return null;
        }

        // 使用 Set 去重，避免重复处理
        Set<String> uniqueNavigate = new HashSet<>(navigate);

        return (c, p) -> {
            for (String key : uniqueNavigate) {
                SQLActionExpression2<IncludeContext, TProxy> exp = navigateMap.get(key);
                if (exp != null) {
                    exp.apply(c, p);
                }
            }
        };
    }

    @Override
    public <S extends T> void insert(S entity) {
        easyEntityQuery.insertable(entity).executeRows();
    }

    @Override
    public <S extends T> void update(S entity) {
        easyEntityQuery.updatable(entity).executeRows();
    }

    @Override
    public <S extends T> void update(S entity, SQLFuncExpression1<TProxy, SQLSelectExpression> columns) {
        easyEntityQuery.updatable(entity).setColumns(columns).executeRows();
    }

    @Override
    public void updateById(UUID id, SQLActionExpression1<TProxy> columns) {
        easyEntityQuery.updatable(entityType).setColumns(columns).whereById(id).executeRows();
    }

//    @Override
//    public <S extends T> void save(S entity) {
//        easyEntityQuery.insertable(entity).onConflictThen(o -> fetcherProvider.apply(o).allFields()).executeRows();
//    }

    @Override
    public <S extends T> void save(S entity) {
        // 只有 track + savable，才能实现聚合根保存
        easyEntityQuery.savable(entity).executeCommand();
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
    public Optional<T> getById(
            UUID id, @Nullable SQLActionExpression2<IncludeContext, TProxy> navigate,
            boolean tracking) {
        EntityQueryable<TProxy, T> queryable = queryable();
        if (navigate != null) {
            queryable = queryable.include2(navigate);
        }
        if (tracking) {
            queryable = queryable.asTracking();
        }
        return queryable.whereById(id).singleOptional();
    }

    @Override
    public Optional<T> getById(UUID id, List<String> navigate, boolean tracking) {
        SQLActionExpression2<IncludeContext, TProxy> navigateExpression = processNavigateList(navigate);
        return getById(id, navigateExpression, tracking);
    }

    @Override
    public Optional<T> getSingle(SQLActionExpression1<TProxy> whereExpression) {
        return queryable().where(whereExpression).singleOptional();
    }

    @Override
    public Optional<T> getSingle(
            SQLActionExpression1<TProxy> whereExpression,
            @Nullable SQLActionExpression2<IncludeContext, TProxy> navigate) {
        EntityQueryable<TProxy, T> queryable = queryable();
        if (navigate != null) {
            queryable = queryable.include2(navigate);
        }
        return queryable.where(whereExpression).singleOptional();
    }

    @Override
    public Optional<T> getSingle(SQLActionExpression1<TProxy> whereExpression, List<String> navigate) {
        SQLActionExpression2<IncludeContext, TProxy> navigateExpression = processNavigateList(navigate);
        return getSingle(whereExpression, navigateExpression);
    }

    @Override
    public T getSingleNotNull(SQLActionExpression1<TProxy> whereExpression) {
        return queryable().where(whereExpression).singleNotNull();
    }

    @Override
    public Optional<T> getFirst(SQLActionExpression1<TProxy> whereExpression, SQLActionExpression1<TProxy> order) {
        return Optional.ofNullable(queryable().where(whereExpression).orderBy(order).firstOrNull());
    }

    @Override
    public Optional<T> getFirst(
            SQLActionExpression1<TProxy> whereExpression,
            @Nullable SQLActionExpression2<IncludeContext, TProxy> navigate, SQLActionExpression1<TProxy> order) {
        EntityQueryable<TProxy, T> queryable = queryable();
        if (navigate != null) {
            queryable = queryable.include2(navigate);
        }
        return Optional.ofNullable(queryable.where(whereExpression).orderBy(order).firstOrNull());
    }

    @Override
    public Optional<T> getFirst(
            SQLActionExpression1<TProxy> whereExpression, List<String> navigate,
            SQLActionExpression1<TProxy> order) {
        SQLActionExpression2<IncludeContext, TProxy> navigateExpression = processNavigateList(navigate);
        return getFirst(whereExpression, navigateExpression, order);
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
    public PageResult<T> getPagination(Object whereObject, Pageable pageable) {
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
