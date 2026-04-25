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
public interface ClosureOperation<T, TProxy extends ProxyEntity<TProxy, T>, U, UProxy extends ProxyEntity<UProxy, U>> {

    /**
     * 查询直接子节点
     *
     * @param parentId 父节点ID
     * @return 直接子节点列表
     */
    List<T> getDirectChildren(UUID parentId);

    /**
     * 查询所有后代节点（包括多级）
     *
     * @param parentId 父节点ID
     * @return 所有后代节点列表
     */
    List<T> getDescendants(UUID parentId);

    /**
     * 查询直接父节点
     *
     * @param childId 子节点ID
     * @return 直接父节点
     */
    Optional<T> getDirectParent(UUID childId);

    /**
     * 查询所有祖先节点
     *
     * @param childId 子节点ID
     * @return 所有祖先节点列表
     */
    List<T> getAllAncestors(UUID childId);

    /**
     * 移动节点到新的父节点下
     *
     * @param nodeId      要移动的节点ID
     * @param newParentId 新的父节点ID
     * @return 移动后的节点
     */
    T moveNode(UUID nodeId, UUID newParentId);

    /**
     * 获取树状结构
     *
     * @param rootId 根节点ID
     * @return 根节点（包含子节点）
     */
    T getTree(UUID rootId);
}
