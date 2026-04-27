package com.lonbon.cloud.base.service;

import com.easy.query.core.proxy.ProxyEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 闭包服务接口，定义了树形结构的操作方法
 * <p>
 * 该接口提供了对树形结构的各种操作，包括查询上下级、移动节点、删除节点等功能。
 * 适用于需要处理层级关系的实体，如租户、部门等。
 * </p>
 *
 * @param <T> 实体类型
 * @author lonbon
 * @since 1.0.0
 */
public interface ClosureService<T, TProxy extends ProxyEntity<TProxy, T>, U, UProxy extends ProxyEntity<UProxy, U>> 
    extends ClosureOperation<T, TProxy, U, UProxy> {
    
    ClosureOperation<T, TProxy, U, UProxy> getClosureOperation();
    
    @Override
    default List<T> getDirectChildren(UUID parentId) {
        return getClosureOperation().getDirectChildren(parentId);
    }

    @Override
    default List<T> getDescendants(UUID parentId) {
        return getClosureOperation().getDescendants(parentId);
    }

    @Override
    default Optional<T> getDirectParent(UUID childId) {
        return getClosureOperation().getDirectParent(childId);
    }

    @Override
    default List<T> getAllAncestors(UUID childId) {
        return getClosureOperation().getAllAncestors(childId);
    }

    @Override
    default T moveNode(UUID nodeId, UUID newParentId) {
        return getClosureOperation().moveNode(nodeId, newParentId);
    }

    @Override
    default T getTree(UUID rootId) {
        return getClosureOperation().getTree(rootId);
    }
}