package com.lonbon.cloud.base.service;

import com.easy.query.core.exception.EasyQuerySingleMoreElementException;
import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.expression.lambda.SQLFuncExpression1;
import com.easy.query.core.proxy.ProxyEntity;
import com.easy.query.core.proxy.SQLSelectExpression;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;
import com.lonbon.cloud.base.exception.BusinessException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * 服务接口，定义了对实体的业务操作方法。
 * <p>
 * 该接口提供了对实体的创建、更新、删除、查询等业务操作，
 * 是业务逻辑层与数据访问层之间的桥梁。
 * </p>
 *
 * @param <T>      实体类型，表示该服务管理的实体类型
 * @param <TProxy> 实体代理类型，用于构建类型安全的查询条件
 * @author lonbon
 * @since 1.0.0
 */
public interface EntityService<T, TProxy extends ProxyEntity<TProxy, T>> {
    /**
     * 创建实体。
     * <p>
     * 根据给定的创建DTO创建新的实体。
     * </p>
     *
     * @param createDto 创建DTO对象，包含实体的初始数据
     * @return 创建的实体
     * @throws IllegalArgumentException 如果 createDto 为 null
     */
    T createEntity(Object createDto);

    /**
     * 更新实体。
     * <p>
     * 根据给定的ID和更新DTO更新实体。
     * </p>
     *
     * @param id        实体的唯一标识符
     * @param updateDto 更新DTO对象，包含要更新的字段
     * @throws IllegalArgumentException 如果 id 为 null 或 updateDto 为 null
     * @throws BusinessException        如果实体不存在
     */
    void updateEntity(UUID id, Object updateDto);

    /**
     * 更新实体。
     * <p>
     * 根据给定的ID和更新函数更新实体。
     * </p>
     *
     * @param id         实体的唯一标识符
     * @param updateFunc 更新函数，用于修改实体
     * @throws IllegalArgumentException 如果 id 为 null 或 updateFunc 为 null
     * @throws BusinessException        如果实体不存在
     */
    void updateEntity(UUID id, Function<T, T> updateFunc);


    void updateEntity(UUID id, SQLActionExpression1<TProxy> columns);


    <S extends T> void updateEntity(S entity, SQLFuncExpression1<TProxy, SQLSelectExpression> columns);

    /**
     * 删除实体。
     * <p>
     * 根据给定的ID删除实体。
     * </p>
     *
     * @param id 实体的唯一标识符
     * @throws IllegalArgumentException 如果 id 为 null
     */
    void deleteEntity(UUID id);

    /**
     * 通过ID获取实体。
     * <p>
     * 根据给定的ID获取实体，如果未找到则返回 Optional.empty()。
     * </p>
     *
     * @param id 实体的唯一标识符
     * @return 包含实体的 Optional，如果未找到则为 Optional.empty()
     * @throws IllegalArgumentException 如果 id 为 null
     */
    Optional<T> getEntityById(UUID id);

    /**
     * 通过ID获取实体，可指定导航属性和是否开启追踪。
     * <p>
     * 根据给定的ID获取实体，可指定导航属性用于关联查询，以及是否开启实体追踪。
     * </p>
     *
     * @param id       实体的唯一标识符
     * @param navigate 导航属性表达式，用于关联查询
     * @param tracking 是否开启实体追踪
     * @return 包含实体的 Optional，如果未找到则为 Optional.empty()
     * @throws IllegalArgumentException 如果 id 为 null
     */
    Optional<T> getEntityById(UUID id, SQLActionExpression2<IncludeContext, TProxy> navigate, boolean tracking);

    /**
     * 通过ID获取实体，可指定导航属性名称列表和是否开启追踪。
     * <p>
     * 根据给定的ID获取实体，可指定导航属性名称列表用于关联查询，以及是否开启实体追踪。
     * </p>
     *
     * @param id       实体的唯一标识符
     * @param navigate 导航属性名称列表，用于关联查询
     * @param tracking 是否开启实体追踪
     * @return 包含实体的 Optional，如果未找到则为 Optional.empty()
     * @throws IllegalArgumentException 如果 id 为 null
     */
    Optional<T> getEntityById(UUID id, List<String> navigate, boolean tracking);

    /**
     * 根据条件获取单个实体。
     * <p>
     * 根据给定的查询条件获取单个实体，如果未找到则返回 Optional.empty()，
     * 如果找到多个则抛出异常。
     * </p>
     *
     * @param whereExpression 查询条件表达式
     * @return 包含实体的 Optional，如果未找到则为 Optional.empty()
     * @throws IllegalArgumentException            如果 whereExpression 为 null
     * @throws EasyQuerySingleMoreElementException 如果查询结果大于一条数据
     */
    Optional<T> getEntity(SQLActionExpression1<TProxy> whereExpression);

    /**
     * 根据条件获取单个实体，可指定导航属性。
     * <p>
     * 根据给定的查询条件获取单个实体，可指定导航属性用于关联查询，
     * 如果未找到则返回 Optional.empty()，如果找到多个则抛出异常。
     * </p>
     *
     * @param whereExpression 查询条件表达式
     * @param navigate        导航属性表达式，用于关联查询
     * @return 包含实体的 Optional，如果未找到则为 Optional.empty()
     * @throws IllegalArgumentException            如果 whereExpression 为 null
     * @throws EasyQuerySingleMoreElementException 如果查询结果大于一条数据
     */
    Optional<T> getEntity(
            SQLActionExpression1<TProxy> whereExpression, SQLActionExpression2<IncludeContext, TProxy> navigate);

    /**
     * 根据条件获取单个实体，可指定导航属性名称列表。
     * <p>
     * 根据给定的查询条件获取单个实体，可指定导航属性名称列表用于关联查询，
     * 如果未找到则返回 Optional.empty()，如果找到多个则抛出异常。
     * </p>
     *
     * @param whereExpression 查询条件表达式
     * @param navigate        导航属性名称列表，用于关联查询
     * @return 包含实体的 Optional，如果未找到则为 Optional.empty()
     * @throws IllegalArgumentException            如果 whereExpression 为 null
     * @throws EasyQuerySingleMoreElementException 如果查询结果大于一条数据
     */
    Optional<T> getEntity(SQLActionExpression1<TProxy> whereExpression, List<String> navigate);

    /**
     * 根据条件获取第一个实体。
     * <p>
     * 根据给定的查询条件获取第一个实体，如果未找到则返回 Optional.empty()，
     * 如果找到多个则返回第一个，不会抛出异常。
     * </p>
     *
     * @param whereExpression 查询条件表达式
     * @param order           排序条件表达式，不能为 {@literal null}
     * @return 包含第一个实体的 Optional，如果未找到则为 Optional.empty()
     * @throws IllegalArgumentException 如果 whereExpression 为 null
     */
    Optional<T> getFirstEntity(SQLActionExpression1<TProxy> whereExpression, SQLActionExpression1<TProxy> order);

    /**
     * 根据条件获取第一个实体，可指定导航属性。
     * <p>
     * 根据给定的查询条件获取第一个实体，可指定导航属性用于关联查询，
     * 如果未找到则返回 Optional.empty()，如果找到多个则返回第一个，不会抛出异常。
     * </p>
     *
     * @param whereExpression 查询条件表达式
     * @param navigate        导航属性表达式，用于关联查询
     * @param order           排序条件表达式，不能为 {@literal null}
     * @return 包含第一个实体的 Optional，如果未找到则为 Optional.empty()
     * @throws IllegalArgumentException 如果 whereExpression 为 null
     */
    Optional<T> getFirstEntity(
            SQLActionExpression1<TProxy> whereExpression, SQLActionExpression2<IncludeContext, TProxy> navigate,
            SQLActionExpression1<TProxy> order);

    /**
     * 根据条件获取第一个实体，可指定导航属性名称列表。
     * <p>
     * 根据给定的查询条件获取第一个实体，可指定导航属性名称列表用于关联查询，
     * 如果未找到则返回 Optional.empty()，如果找到多个则返回第一个，不会抛出异常。
     * </p>
     *
     * @param whereExpression 查询条件表达式
     * @param navigate        导航属性名称列表，用于关联查询
     * @param order           排序条件表达式，不能为 {@literal null}
     * @return 包含第一个实体的 Optional，如果未找到则为 Optional.empty()
     * @throws IllegalArgumentException 如果 whereExpression 为 null
     */
    Optional<T> getFirstEntity(
            SQLActionExpression1<TProxy> whereExpression, List<String> navigate, SQLActionExpression1<TProxy> order);

    /**
     * 获取所有实体。
     * <p>
     * 返回所有该类型的实体。
     * </p>
     *
     * @return 所有实体的列表
     */
    List<T> getAllEntities();

    /**
     * 获取分页实体。
     * <p>
     * 根据给定的查询条件和分页参数获取实体的分页结果。
     * </p>
     *
     * @param whereObject 查询条件对象
     * @param pageable    分页参数
     * @return 分页结果
     * @throws IllegalArgumentException 如果 pageable 为 null
     */
    PageResult<T> getPaginationEntities(Object whereObject, Pageable pageable);
}
