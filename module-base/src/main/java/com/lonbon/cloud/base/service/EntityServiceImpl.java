package com.lonbon.cloud.base.service;

import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.expression.lambda.SQLFuncExpression1;
import com.easy.query.core.proxy.AbstractProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.easy.query.core.proxy.SQLSelectExpression;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;
import com.lonbon.cloud.base.exception.BusinessException;
import com.lonbon.cloud.base.exception.ErrorCode;
import com.lonbon.cloud.base.repository.Repository;
import io.github.linpeilie.Converter;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;

/**
 * 简单实体服务基类
 * <p>
 * 提供了对实体的基本CRUD操作的默认实现。
 * 使用MapStruct进行DTO与实体之间的转换，使用Repository进行数据访问。
 * 所有公共方法均开启事务支持，确保数据一致性。
 * </p>
 *
 * @param <T>      实体类型
 * @param <TProxy> 实体代理类型
 * @author lonbon
 * @since 1.0.0
 */
@Transactional(rollbackFor = Exception.class)
public abstract class EntityServiceImpl<T extends ProxyEntityAvailable<T, TProxy>,
        TProxy extends AbstractProxyEntity<TProxy, T>>
        implements EntityService<T, TProxy> {

    /**
     * MapStruct转换器，用于DTO与实体之间的转换
     */
    protected final Converter converter;

    /**
     * 数据仓库，用于数据访问
     */
    protected final Repository<T, TProxy> repository;

    /**
     * 实体类型Class
     */
    protected final Class<T> entityType;

    /**
     * 拦截器列表，用于扩展实体服务功能
     */
    protected final List<EntityServiceInterceptor<T>> interceptors = new ArrayList<>();

    /**
     * 拦截器是否已初始化的标志
     */
    private volatile boolean interceptorsInitialized = false;

    /**
     * 构造简单实体服务
     *
     * @param converter  转换器
     * @param repository 数据仓库
     * @param entityType 实体类型
     */
    public EntityServiceImpl(Converter converter, Repository<T, TProxy> repository, Class<T> entityType) {
        this.converter = converter;
        this.repository = repository;
        this.entityType = entityType;
    }

    /**
     * 确保拦截器已初始化
     * <p>
     * 使用懒加载方式，在第一次需要拦截器时进行收集。
     * 此时子类已完全构造完成，可以通过反射获取所有实现了EntityServiceInterceptor的字段。
     * </p>
     */
    private void ensureInterceptorsInitialized() {
        if (!interceptorsInitialized) {
            synchronized (this) {
                if (!interceptorsInitialized) {
                    collectInterceptors();
                    interceptorsInitialized = true;
                }
            }
        }
    }

    /**
     * 收集子类中实现了EntityServiceInterceptor的字段
     */
    private void collectInterceptors() {
        Class<?> currentClass = this.getClass();
        while (currentClass != EntityServiceImpl.class && currentClass != Object.class) {
            for (var field : currentClass.getDeclaredFields()) {
                if (EntityServiceInterceptor.class.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(this);
                        if (value != null) {
                            @SuppressWarnings("unchecked")
                            EntityServiceInterceptor<T> interceptor = (EntityServiceInterceptor<T>) value;
                            interceptors.add(interceptor);
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to collect interceptor from field: " + field.getName(), e);
                    }
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    public List<EntityServiceInterceptor<T>> getInterceptors() {
        ensureInterceptorsInitialized();
        return interceptors;
    }

    public Repository<T, TProxy> getEntityRepository() {
        return repository;
    }

    /**
     * 创建实体
     * <p>
     * 使用MapStruct将DTO转换为实体，然后插入数据库。
     * </p>
     *
     * @param createDto 创建DTO
     * @return 创建的实体
     */
    @Override
    public T createEntity(Object createDto) {
        T entity = converter.convert(createDto, entityType);
        List<EntityServiceInterceptor<T>> interceptors = getInterceptors();
        for (EntityServiceInterceptor<T> interceptor : interceptors) {
            interceptor.preCreate(entity);
        }
        repository.insert(entity);
        for (EntityServiceInterceptor<T> interceptor : interceptors) {
            interceptor.postCreate(entity);
        }
        return entity;
    }

    /**
     * 更新实体
     * <p>
     * 根据ID查找实体，将DTO转换为实体后更新。
     * </p>
     *
     * @param id        实体ID
     * @param updateDto 更新DTO
     */
    @Override
    public void updateEntity(UUID id, Object updateDto) {
        this.updateEntity(id, (Function<T, T>) entity -> converter.convert(updateDto, entity));
    }

    /**
     * 更新实体
     * <p>
     * 根据ID查找实体，使用更新函数修改后更新。
     * 在追踪上下文中执行，确保修改的实体与数据库记录一致。
     * </p>
     *
     * @param id         实体ID
     * @param updateFunc 更新函数
     * @throws BusinessException 如果实体不存在
     */
    @Override
    public void updateEntity(UUID id, Function<T, T> updateFunc) {
        repository.track(() -> {
            T existing = repository.getById(id, true).orElseThrow(
                    () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Entity not found, ID: " + id));

            T updated = updateFunc.apply(existing);
            repository.update(updated);
        });
    }

    /**
     * 根据ID更新实体指定列
     *
     * @param id      实体ID
     * @param columns 要更新的列表达式
     */
    @Override
    public void updateEntity(UUID id, SQLActionExpression1<TProxy> columns) {
        repository.updateById(id, columns);
    }

    /**
     * 更新实体指定列
     *
     * @param entity  要更新的实体
     * @param columns 要更新的列表达式
     * @param <S>     实体类型
     */
    @Override
    public <S extends T> void updateEntity(S entity, SQLFuncExpression1<TProxy, SQLSelectExpression> columns) {
        repository.update(entity, columns);
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
    public Optional<T> getEntity(SQLActionExpression1<TProxy> whereExpression, List<String> navigate) {
        return repository.getSingle(whereExpression, navigate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Optional<T> getFirstEntity(
            SQLActionExpression1<TProxy> whereExpression,
            SQLActionExpression1<TProxy> order) {
        return repository.getFirst(whereExpression, order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Optional<T> getFirstEntity(
            SQLActionExpression1<TProxy> whereExpression, SQLActionExpression2<IncludeContext, TProxy> navigate,
            SQLActionExpression1<TProxy> order) {
        return repository.getFirst(whereExpression, navigate, order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Optional<T> getFirstEntity(
            SQLActionExpression1<TProxy> whereExpression, List<String> navigate,
            SQLActionExpression1<TProxy> order) {
        return repository.getFirst(whereExpression, navigate, order);
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
