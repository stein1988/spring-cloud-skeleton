package com.lonbon.cloud.base.repository;

import com.easy.query.core.exception.EasyQuerySingleMoreElementException;
import com.easy.query.core.exception.EasyQuerySingleNotNullException;
import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 仓库接口，定义了对实体的基本操作方法。
 * <p>
 * 该接口提供了对聚合根实体的基本CRUD操作，包括插入、保存、查询、删除等功能。
 * 支持基于条件的灵活查询、分页查询以及单条记录查询等场景。
 * </p>
 *
 * @param <TProxy> 实体代理类型，用于构建类型安全的查询条件
 * @param <T>      实体类型，表示该仓库管理的聚合根类型
 * @author lonbon
 * @since 1.0.0
 */
public interface Repository<TProxy, T> {

    /**
     * 直接插入给定的实体。
     * <p>
     * 该方法会直接执行INSERT操作，不会检查实体是否已存在。
     * 适用于确定实体不存在于数据库中的场景。
     * </p>
     *
     * @param entity 要插入的实体，不能为 {@literal null}
     * @param <S>    实体类型的子类型
     * @return 插入后的实体，可能包含数据库生成的主键等信息
     * @throws IllegalArgumentException 如果给定的 {@literal entity} 为 {@literal null}
     */
    <S extends T> S insert(S entity);

    /**
     * 更新给定的实体。
     * <p>
     * 该方法会直接执行UPDATE操作，用于更新已存在的实体。
     * 适用于确定实体已存在于数据库中的场景。
     * </p>
     *
     * @param entity 要更新的实体，不能为 {@literal null}
     * @param <S>    实体类型的子类型
     * @return 更新后的实体
     * @throws IllegalArgumentException 如果给定的 {@literal entity} 为 {@literal null}
     * @throws OptimisticLockingFailureException 当实体使用乐观锁并且版本属性与持久化存储中的值不同时，
     *                                           或者当实体被认为存在但数据库中不存在时
     */
    <S extends T> S update(S entity);

    /**
     * 保存给定的实体。
     * <p>
     * 该方法会判断实体是新增还是更新：如果实体的主键不存在则执行INSERT，
     * 如果主键已存在则执行UPDATE。使用返回的实例进行进一步操作，
     * 因为保存操作可能完全改变了实体实例。
     * </p>
     *
     * @param entity 要保存的实体，不能为 {@literal null}
     * @param <S>    实体类型的子类型
     * @return 保存后的实体
     * @throws IllegalArgumentException 如果给定的 {@literal entity} 为 {@literal null}
     * @throws OptimisticLockingFailureException 当实体使用乐观锁并且版本属性与持久化存储中的值不同时，
     *                                           或者当实体被认为存在但数据库中不存在时
     */
    <S extends T> S save(S entity);

    /**
     * 保存所有给定的实体。
     * <p>
     * 批量保存实体，对于每个实体会判断是新增还是更新。
     * </p>
     *
     * @param entities 要保存的实体集合，不能为 {@literal null}，也不能包含 {@literal null}
     * @param <S>      实体类型的子类型
     * @return 保存后的实体集合，大小与传入参数相同
     * @throws IllegalArgumentException 如果给定的 {@link Iterable entities} 或其中一个实体为 {@literal null}
     * @throws OptimisticLockingFailureException 当至少一个实体使用乐观锁并且版本属性与持久化存储中的值不同时
     */
    <S extends T> Iterable<S> saveAll(Iterable<S> entities);

    /**
     * 通过 ID 检索实体。
     *
     * @param id 实体的唯一标识符，不能为 {@literal null}
     * @return 具有给定 ID 的实体包装在 {@link Optional} 中，如果未找到则返回 {@link Optional#empty()}
     * @throws IllegalArgumentException 如果 {@literal id} 为 {@literal null}
     */
    Optional<T> findById(UUID id);

    Optional<T> findById(UUID id, boolean tracking);

    /**
     * 返回是否存在具有给定 ID 的实体。
     *
     * @param id 实体的唯一标识符，不能为 {@literal null}
     * @return 如果存在具有给定 ID 的实体则返回 {@literal true}，否则返回 {@literal false}
     * @throws IllegalArgumentException 如果 {@literal id} 为 {@literal null}
     */
    boolean existsById(UUID id);

    /**
     * 返回所有该类型的实例。
     *
     * @return 所有实体的可迭代对象
     */
    List<T> findAll();

    /**
     * 返回具有给定 ID 的所有类型 {@code T} 的实例。
     * <p>
     * 如果某些或所有 ID 未找到，则不会为这些 ID 返回实体。
     * 请注意，结果中元素的顺序不保证与传入 ID 的顺序一致。
     * </p>
     *
     * @param ids 实体ID集合，不能为 {@literal null}，也不能包含任何 {@literal null} 值
     * @return 找到的实体的可迭代对象，大小可以等于或小于给定 {@literal ids} 的数量
     * @throws IllegalArgumentException 如果给定的 {@link Collection ids} 或其中一个项为 {@literal null}
     */
    List<T> findAllByIds(Collection<UUID> ids);

    /**
     * 根据条件查询所有实体。
     * <p>
     * 使用Lambda表达式构建类型安全的查询条件。
     * </p>
     *
     * @param whereExpression 查询条件表达式，不能为 {@literal null}
     * @return 符合条件的实体列表
     */
    List<T> findAll(SQLActionExpression1<TProxy> whereExpression);

    /**
     * 根据条件查询所有实体（条件可控）。
     * <p>
     * 当 condition 为 true 时应用查询条件，否则返回所有实体。
     * 适用于需要根据运行时条件决定是否过滤的场景。
     * </p>
     *
     * @param condition       是否应用查询条件
     * @param whereExpression 查询条件表达式，不能为 {@literal null}
     * @return 符合条件的实体列表，当 condition 为 false 时返回所有实体
     */
    List<T> findAll(boolean condition, SQLActionExpression1<TProxy> whereExpression);

    /**
     * 返回符合 {@link Pageable} 对象中提供的分页限制的实体分页结果。
     *
     * @param whereObject 查询条件对象，用于构建查询条件
     * @param pageable    分页请求参数，包含页码、每页大小等信息，不能为空
     * @return 分页结果，包含当前页数据和分页元信息
     */
    PageResult<T> findPagination(Object whereObject, Pageable pageable);

    /**
     * 返回单个结果作为 {@link Optional}。
     * <p>
     * 如果没有结果，则返回 {@link Optional#empty()}。
     * 如果有多个结果，则抛出异常。
     * </p>
     *
     * @param whereExpression 查询条件表达式，不能为 {@literal null}
     * @return 包含查询结果的 {@link Optional}，如果没有结果则为 {@link Optional#empty()}
     * @throws IllegalArgumentException 如果 {@literal whereExpression} 为 {@literal null}
     * @throws EasyQuerySingleMoreElementException 如果查询结果大于一条数据
     */
    Optional<T> singleOptional(SQLActionExpression1<TProxy> whereExpression);

    /**
     * 根据条件查询单个实体，如果不存在则抛出默认异常。
     * <p>
     * 当查询结果为空时，抛出框架默认的运行时异常。
     * 适用于对查询结果必须存在的场景，简化异常处理。
     * </p>
     *
     * @param whereExpression 查询条件表达式，不能为 {@literal null}
     * @return 符合条件的单个实体
     * @throws EasyQuerySingleMoreElementException 如果查询结果大于一条数据
     * @throws EasyQuerySingleNotNullException     如果查询不到数据
     */
    T singleNotNull(SQLActionExpression1<TProxy> whereExpression);

    /**
     * 返回可用实体的数量。
     *
     * @return 实体的总数量
     */
    long count();

    /**
     * 删除给定的实体。
     *
     * @param entity 要删除的实体，不能为 {@literal null}
     * @throws IllegalArgumentException 如果给定的实体为 {@literal null}
     * @throws OptimisticLockingFailureException 当实体使用乐观锁并且版本属性与持久化存储中的值不同时，
     *                                           或者当实体被认为存在但数据库中不存在时
     */
    void delete(T entity);

    /**
     * 删除具有给定 ID 的实体。
     * <p>
     * 如果在持久化存储中未找到实体，则静默忽略，不会抛出异常。
     * </p>
     *
     * @param id 实体的唯一标识符，不能为 {@literal null}
     * @throws IllegalArgumentException 如果给定的 {@literal id} 为 {@literal null}
     * @throws OptimisticLockingFailureException 当实体使用乐观锁并且版本属性与持久化存储中的值不同时，
     *                                           或者当实体被认为存在但数据库中不存在时
     */
    void deleteById(UUID id);

    /**
     * 删除给定的所有实体。
     *
     * @param entities 要删除的实体集合，不能为 {@literal null}，也不能包含 {@literal null} 元素
     * @throws IllegalArgumentException 如果给定的 {@literal entities} 或其中一个实体为 {@literal null}
     * @throws OptimisticLockingFailureException 当至少一个实体使用乐观锁并且版本属性与持久化存储中的值不同时
     */
    void deleteAll(Iterable<? extends T> entities);

    /**
     * 删除由仓库管理的所有实体。
     * <p>
     * 该方法会删除该类型的所有实体记录，请谨慎使用。
     * </p>
     */
    void deleteAll();

    /**
     * 删除具有给定 ID 的所有实体。
     * <p>
     * 在持久化存储中未找到的实体将被静默忽略。
     * </p>
     *
     * @param ids 实体ID集合，不能为 {@literal null}，也不能包含 {@literal null} 元素
     * @throws IllegalArgumentException 如果给定的 {@literal ids} 或其中一个元素为 {@literal null}
     * @throws OptimisticLockingFailureException 当至少一个实体使用乐观锁并且版本属性与持久化存储中的值不同时
     */
    void deleteAllById(Iterable<? extends UUID> ids);

    <R> R track(Supplier<R> supplier);

    void track(Runnable runnable);
}