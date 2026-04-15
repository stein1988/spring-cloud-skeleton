package com.lonbon.cloud.base.service;

import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.proxy.AbstractProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.exception.BusinessException;
import com.lonbon.cloud.base.exception.ErrorCode;
import com.lonbon.cloud.base.repository.Repository;
import io.github.linpeilie.Converter;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional(rollbackFor = Exception.class)
public abstract class ClosureEntityService<T extends ProxyEntityAvailable<T, TProxy> & ClosureAvailable<U>,
        TProxy extends AbstractProxyEntity<TProxy, T>, U extends ProxyEntityAvailable<U, UProxy> & Closure,
        UProxy extends AbstractProxyEntity<UProxy, U>>
        extends SimpleEntityService<T, TProxy> implements ClosureService<T, TProxy, U, UProxy> {

    private final static String CLOSURE_ANCESTOR_ID = "ancestorId";

    private final static String CLOSURE_DESCENDANT_ID = "descendantId";

    private final static String CLOSURE_DISTANCE = "distance";

    protected final Repository<U, UProxy> closureRepository;

    public ClosureEntityService(
            Converter converter, Repository<T, TProxy> repository, Repository<U, UProxy> closureRepository,
            Class<T> entityType) {
        super(converter, repository, entityType);
        this.closureRepository = closureRepository;
    }

    protected abstract SQLActionExpression2<IncludeContext, TProxy> navigate();

    protected abstract U createClosure(UUID ancestorId, UUID descendantId, int distance);

    /**
     * 创建实体并构建闭包表关系
     * <p>
     * 闭包表（Closure Table）是一种存储树形结构层次关系的模式。
     * 每条闭包记录包含：祖先节点ID、后代节点ID、距离（层次差）。
     * 例如：节点A是根节点，节点B是A的子节点，节点C是B的子节点，则：
     * - A到A的距离为0（自引用）
     * - A到B的距离为1
     * - A到C的距离为2
     * - B到C的距离为1
     *
     * @param createDto 创建DTO对象
     * @return 创建的实体
     */
    @Override
    public T createEntity(Object createDto) {
        T created = super.createEntity(createDto);
        UUID id = created.getId();

        List<U> closures = new ArrayList<>();
        closures.add(createClosure(id, id, 0));

        UUID parentId = created.getParentId();
        if (parentId != null) {
            T parent = repository.getById(parentId, navigate(), false).orElseThrow(
                    () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                                                "Parent entity not found, ID: " + parentId));

            closures.add(createClosure(parentId, id, 1));

            List<U> ancestors = parent.getAncestors();
            if (ancestors != null && !ancestors.isEmpty()) {
                for (U ancestor : ancestors) {
                    closures.add(createClosure(ancestor.getAncestorId(), id, ancestor.getDistance() + 1));
                }
            }
        }

        for (U closure : closures) {
            closureRepository.insert(closure);
        }

        return created;
    }

    /**
     * 查询直接子节点
     *
     * @param parentId 父节点ID
     * @return 直接子节点列表
     */
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<T> getDirectChildren(UUID parentId) {
        // 先查询闭包表中 ancestorId = parentId 且 distance = 1 的记录
        List<U> childClosures = closureRepository.getAll(u -> {
            u.anyColumn(CLOSURE_ANCESTOR_ID).eq(parentId);
            u.anyColumn(CLOSURE_DISTANCE).eq(1);
        });

        // 提取子节点ID
        List<UUID> childIds = childClosures.stream().map(U::getDescendantId).collect(Collectors.toList());

        // 查询子节点实体
        if (childIds.isEmpty()) {
            return new ArrayList<>();
        }

        return repository.getAllByIds(childIds);
    }

    /**
     * 查询所有子节点（包括多级）
     *
     * @param parentId 父节点ID
     * @return 所有子节点列表
     */
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<T> getAllChildren(UUID parentId) {
        // 查询闭包表中所有 ancestorId = parentId 且 distance > 0 的记录
        List<U> childClosures = closureRepository.getAll(u -> {
            u.anyColumn(CLOSURE_ANCESTOR_ID).eq(parentId);
            u.anyColumn(CLOSURE_DISTANCE).gt(0);
        });

        // 提取子节点ID
        List<UUID> childIds = childClosures.stream().map(U::getDescendantId).collect(Collectors.toList());

        // 查询子节点实体
        if (childIds.isEmpty()) {
            return new ArrayList<>();
        }

        return repository.getAllByIds(childIds);
    }

    /**
     * 查询直接父节点
     *
     * @param childId 子节点ID
     * @return 直接父节点
     */
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Optional<T> getDirectParent(UUID childId) {
        // 查询闭包表中 descendantId = childId 且 distance = 1 的记录
        List<U> parentClosures = closureRepository.getAll(u -> {
            u.anyColumn(CLOSURE_DESCENDANT_ID).eq(childId);
            u.anyColumn(CLOSURE_DISTANCE).eq(1);
        });

        if (parentClosures.isEmpty()) {
            return Optional.empty();
        }

        // 提取父节点ID
        UUID parentId = parentClosures.getFirst().getAncestorId();

        // 查询父节点实体
        return repository.getById(parentId);
    }

    /**
     * 查询所有祖先节点
     *
     * @param childId 子节点ID
     * @return 所有祖先节点列表
     */
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<T> getAllAncestors(UUID childId) {
        // 查询闭包表中 descendantId = childId 且 distance > 0 的记录
        List<U> ancestorClosures = closureRepository.getAll(u -> {
            u.anyColumn(CLOSURE_DESCENDANT_ID).eq(childId);
            u.anyColumn(CLOSURE_DISTANCE).gt(0);
        });

        // 提取祖先节点ID
        List<UUID> ancestorIds = ancestorClosures.stream().map(U::getAncestorId).collect(Collectors.toList());

        // 查询祖先节点实体
        if (ancestorIds.isEmpty()) {
            return new ArrayList<>();
        }

        return repository.getAllByIds(ancestorIds);
    }

    /**
     * 移动节点到新的父节点下
     *
     * @param nodeId      要移动的节点ID
     * @param newParentId 新的父节点ID
     * @return 移动后的节点
     */
    public T moveNode(UUID nodeId, UUID newParentId) {
        // 验证节点存在
        T node = repository.getById(nodeId, true).orElseThrow(
                () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Node not found, ID: " + nodeId));

        // 验证新父节点存在
        if (newParentId != null) {
            T newParent = repository.getById(newParentId).orElseThrow(
                    () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                                                "New parent not found, ID: " + newParentId));

            // 防止循环引用
            if (isDescendant(nodeId, newParentId)) {
                throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Cannot move node to its own descendant");
            }
        }

        // 先删除该节点及其所有子节点的闭包关系
        List<U> nodeClosures = closureRepository.getAll(u -> u.anyColumn("descendantId", UUID.class).eq(nodeId));
        for (U closure : nodeClosures) {
            closureRepository.delete(closure);
        }

        // 重新创建闭包关系
        List<U> newClosures = new ArrayList<>();
        newClosures.add(createClosure(nodeId, nodeId, 0));

        if (newParentId != null) {
            T newParent = repository.getById(newParentId, navigate(), false).orElseThrow(
                    () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                                                "New parent not found, ID: " + newParentId));

            newClosures.add(createClosure(newParentId, nodeId, 1));

            List<U> parentAncestors = newParent.getAncestors();
            if (parentAncestors != null && !parentAncestors.isEmpty()) {
                for (U ancestor : parentAncestors) {
                    newClosures.add(createClosure(ancestor.getAncestorId(), nodeId, ancestor.getDistance() + 1));
                }
            }
        }

        // 插入新闭包关系
        for (U closure : newClosures) {
            closureRepository.insert(closure);
        }

        // 更新节点的父ID
        node.setParentId(newParentId);
        return repository.update(node);
    }

    /**
     * 删除节点及其所有子节点
     *
     * @param nodeId 节点ID
     */
    @Override
    public void deleteEntity(UUID nodeId) {
        // 先查询该节点的所有后代节点  ancestorId = nodeId and distance > 0
        List<U> descendantClosures = closureRepository.getAll(u -> {
            u.anyColumn(CLOSURE_ANCESTOR_ID).eq(nodeId);
            u.anyColumn(CLOSURE_DISTANCE).gt(0);
        });

        // 提取所有后代节点ID
        List<UUID> ids = descendantClosures.stream().map(U::getDescendantId).collect(Collectors.toList());

        // 添加当前节点ID
        ids.add(nodeId);

        // 删除所有相关的闭包关系  ancestorId in (ids) or descendantId in (ids)
        List<U> allClosures = closureRepository.getAll(u -> u.or(() -> {
            u.anyColumn(CLOSURE_ANCESTOR_ID).in(ids);
            u.anyColumn(CLOSURE_DESCENDANT_ID).in(ids);
        }));

        for (U closure : allClosures) {
            closureRepository.delete(closure);
        }

        // 删除所有节点实体
        for (UUID id : ids) {
            super.deleteEntity(id);
        }
    }

    /**
     * 检查节点是否是另一个节点的后代
     *
     * @param nodeId     节点ID
     * @param ancestorId 祖先节点ID
     * @return 是否是后代
     */
    private boolean isDescendant(UUID nodeId, UUID ancestorId) {
        // ancestorId = ancestorId and descendantId = nodeId
        List<U> closures = closureRepository.getAll(u -> {
            u.anyColumn(CLOSURE_ANCESTOR_ID).eq(ancestorId);
            u.anyColumn(CLOSURE_DESCENDANT_ID).eq(nodeId);
        });
        return !closures.isEmpty();
    }

    /**
     * 构建树状结构
     *
     * @param rootId 根节点ID
     * @return 根节点（包含子节点）
     */
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public T buildTree(UUID rootId) {
        T root = repository.getById(rootId, navigate(), false).orElseThrow(
                () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Root node not found, ID: " + rootId));

        // 递归构建子树
        buildSubTree(root);

        return root;
    }

    /**
     * 递归构建子树
     *
     * @param parent 父节点
     */
    private void buildSubTree(T parent) {
        if (parent == null) return;

        // 查询直接子节点
        List<T> children = getDirectChildren(parent.getId());

        // 为每个子节点递归构建子树
        for (T child : children) {
            buildSubTree(child);
        }
    }
}
