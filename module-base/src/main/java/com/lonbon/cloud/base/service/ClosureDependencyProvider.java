package com.lonbon.cloud.base.service;

import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.expression.lambda.SQLFuncExpression1;
import com.easy.query.core.proxy.AbstractProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.easy.query.core.proxy.SQLSelectExpression;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.repository.Repository;

import java.util.UUID;

/**
 * 闭包功能依赖提供者接口
 * <p>
 * 该接口定义了闭包操作所需的所有依赖和抽象方法。
 * 需要闭包功能的 Service 实现此接口，以提供闭包操作所需的具体依赖。
 * </p>
 *
 * @param <T>      主实体类型
 * @param <TProxy> 主实体代理类型
 * @param <U>      闭包实体类型
 * @param <UProxy> 闭包实体代理类型
 * @author lonbon
 * @since 1.0.0
 */
public interface ClosureDependencyProvider<T extends ProxyEntityAvailable<T, TProxy> & ClosureAvailable<U>,
        TProxy extends AbstractProxyEntity<TProxy, T>, U extends ProxyEntityAvailable<U, UProxy> & Closure,
        UProxy extends AbstractProxyEntity<UProxy, U>> {

    /**
     * 获取闭包实体仓库
     *
     * @return 闭包仓库实例
     */
    Repository<U, UProxy> getClosureRepository();

    /**
     * 获取主实体仓库
     *
     * @return 主实体仓库实例
     */
    Repository<T, TProxy> getEntityRepository();

    /**
     * 创建闭包实体
     *
     * @param ancestorId   祖先节点ID
     * @param descendantId 后代节点ID
     * @param distance     距离（层级差）
     * @return 闭包实体
     */
    U createClosure(UUID ancestorId, UUID descendantId, Integer distance);

    /**
     * 创建基础实体（不含闭包关系）
     * <p>
     * 该方法由实现类提供，用于调用父类 SimpleEntityService 的 createEntity 方法。
     * 通常实现为：{@code return super.createEntity(createDto);}
     * </p>
     *
     * @param createDto 创建DTO对象
     * @return 创建的实体
     */
    T createBaseEntity(Object createDto);

    /**
     * 导航表达式，用于加载关联数据
     *
     * @return 导航表达式
     */
    SQLActionExpression2<IncludeContext, TProxy> navigate();

    /**
     * 设置父ID列的表达式
     *
     * @return 父ID列表达式
     */
    SQLFuncExpression1<TProxy, SQLSelectExpression> setColumnParentId();
}
