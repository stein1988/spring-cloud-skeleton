package com.lonbon.cloud.base.repository;

import com.easy.query.core.expression.lambda.SQLActionExpression1;

import java.util.Collection;
import java.util.Optional;

/**
 * 仓库接口，定义了对实体的基本操作方法
 * @param <TProxy> 实体代理类型
 * @param <T> 实体类型
 * @param <ID> 主键类型
 */
public interface Repository<TProxy, T, ID> {
    /**
     * 直接插入给定的实体。
     *
     * @param entity 要插入的实体，不能为 {@literal null}。
     * @return 插入后的实体。
     * @throws IllegalArgumentException 如果给定的 {@literal entity} 为 {@literal null}。
     */
    <S extends T> S insert(S entity);

    /**
     * 保存给定的实体。使用返回的实例进行进一步操作，因为保存操作可能完全改变了实体实例。
     *
     * @param entity 要保存的实体，不能为 {@literal null}。
     * @return 保存后的实体。
     * @throws IllegalArgumentException 如果给定的 {@literal entity} 为 {@literal null}。
     * @throws OptimisticLockingFailureException 当实体使用乐观锁并且版本属性与持久化存储中的值不同时。
     *                                          当实体被认为存在但数据库中不存在时也会抛出。
     */
    <S extends T> S save(S entity);

    /**
     * 保存所有给定的实体。
     *
     * @param entities 不能为 {@literal null}，也不能包含 {@literal null}。
     * @return 保存后的实体。返回的 {@literal Iterable} 将与传递的参数大小相同。
     * @throws IllegalArgumentException 如果给定的 {@link Iterable entities} 或其中一个实体为 {@literal null}。
     * @throws OptimisticLockingFailureException 当至少一个实体使用乐观锁并且版本属性与持久化存储中的值不同时。
     *                                          当至少一个实体被认为存在但数据库中不存在时也会抛出。
     */
    <S extends T> Iterable<S> saveAll(Iterable<S> entities);

    /**
     * 通过 ID 检索实体。
     *
     * @param id 不能为 {@literal null}。
     * @return 具有给定 ID 的实体，或如果未找到则返回 {@literal Optional#empty()}。
     * @throws IllegalArgumentException 如果 {@literal id} 为 {@literal null}。
     */
    Optional<T> findById(ID id);

    /**
     * 返回是否存在具有给定 ID 的实体。
     *
     * @param id 不能为 {@literal null}。
     * @return 如果存在具有给定 ID 的实体则为 {@literal true}，否则为 {@literal false}。
     * @throws IllegalArgumentException 如果 {@literal id} 为 {@literal null}。
     */
    boolean existsById(ID id);

    /**
     * 返回所有该类型的实例。
     *
     * @return 所有实体
     */
    Iterable<T> findAll();

    /**
     * 返回具有给定 ID 的所有类型 {@code T} 的实例。
     * <p>
     * 如果某些或所有 ID 未找到，则不会为这些 ID 返回实体。
     * <p>
     * 请注意，结果中元素的顺序不保证。
     *
     * @param ids 不能为 {@literal null}，也不能包含任何 {@literal null} 值。
     * @return 找到的实体的可迭代对象。大小可以等于或小于给定 {@literal ids} 的数量。
     * @throws IllegalArgumentException 如果给定的 {@link Iterable ids} 或其中一个项为 {@literal null}。
     */
    Iterable<T> findAllById(Collection<ID> ids);

    /**
     * 根据条件查询所有实体
     *
     * @param whereExpression 查询条件表达式
     * @return 符合条件的实体列表
     */
    Iterable<T> findAll(SQLActionExpression1<TProxy> whereExpression);

    /**
     * 根据条件查询所有实体（条件可控）
     *
     * @param condition 是否应用条件
     * @param whereExpression 查询条件表达式
     * @return 符合条件的实体列表
     */
    Iterable<T> findAll(boolean condition, SQLActionExpression1<TProxy> whereExpression);

    /**
     * 返回按给定选项排序的所有实体。
     *
     * @param sort 排序规范，不能为空
     * @return 按给定选项排序的所有实体
     */
    // TODO: Sort待实现
//    Iterable<T> findAll(Sort sort);

    /**
     * 返回符合 {@link Pageable} 对象中提供的分页限制的实体 {@link Page}。
     *
     * @param pageable 分页请求，不能为空
     * @return 实体页
     */
    // TODO: Page、Pageable待实现
//    Page<T> findAll(Pageable pageable);

    /**
     * 返回可用实体的数量。
     *
     * @return 实体的数量。
     */
    long count();

    /**
     * 删除给定的实体。
     *
     * @param entity 不能为 {@literal null}。
     * @throws IllegalArgumentException 如果给定的实体为 {@literal null}。
     * @throws OptimisticLockingFailureException 当实体使用乐观锁并且版本属性与持久化存储中的值不同时。
     *                                          当实体被认为存在但数据库中不存在时也会抛出。
     */
    void delete(T entity);

    /**
     * 删除具有给定 ID 的实体。
     * <p>
     * 如果在持久化存储中未找到实体，则静默忽略。
     *
     * @param id 不能为 {@literal null}。
     * @throws IllegalArgumentException 如果给定的 {@literal id} 为 {@literal null}
     * @throws OptimisticLockingFailureException 当实体使用乐观锁并且版本属性与持久化存储中的值不同时。
     *                                          当实体被认为存在但数据库中不存在时也会抛出。
     */
    void deleteById(ID id);

    /**
     * 删除给定的实体。
     *
     * @param entities 不能为 {@literal null}。不能包含 {@literal null} 元素。
     * @throws IllegalArgumentException 如果给定的 {@literal entities} 或其中一个实体为 {@literal null}。
     * @throws OptimisticLockingFailureException 当至少一个实体使用乐观锁并且版本属性与持久化存储中的值不同时。
     *                                          当至少一个实体被认为存在但数据库中不存在时也会抛出。
     */
    void deleteAll(Iterable<? extends T> entities);

    /**
     * 删除由仓库管理的所有实体。
     */
    void deleteAll();

    /**
     * 删除具有给定 ID 的所有类型 {@code T} 的实例。
     * <p>
     * 在持久化存储中未找到的实体将被静默忽略。
     *
     * @param ids 不能为 {@literal null}。不能包含 {@literal null} 元素。
     * @throws IllegalArgumentException 如果给定的 {@literal ids} 或其中一个元素为 {@literal null}。
     * @throws OptimisticLockingFailureException 当实体使用乐观锁并且版本属性与持久化存储中的值不同时。
     *                                          当实体被认为存在但数据库中不存在时也会抛出。
     */
    void deleteAllById(Iterable<? extends ID> ids);
}
