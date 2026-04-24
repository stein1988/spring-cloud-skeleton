package com.lonbon.cloud.base.service;

import com.easy.query.core.proxy.AbstractProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.exception.BusinessException;
import com.lonbon.cloud.base.exception.ErrorCode;
import com.lonbon.cloud.base.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 闭包操作接口
 * <p>
 * 该接口使用 Java 8 default 方法提供闭包（Closure Table）树形结构的默认实现。
 * 需要闭包功能的 Service 只需实现此接口，并实现 ClosureDependencyProvider 接口提供依赖。
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * public class DepartmentServiceImpl
 *         extends SimpleEntityService<Department, DepartmentProxy>
 *         implements ClosureOperations<Department, DepartmentProxy, DepartmentClosure, DepartmentClosureProxy> {
 *
 *     private final Repository<DepartmentClosure, DepartmentClosureProxy> closureRepository;
 *
 *     // 实现 ClosureDependencyProvider 的抽象方法
 *     @Override
 *     public Repository<DepartmentClosure, DepartmentClosureProxy> getClosureRepository() {
 *         return closureRepository;
 *     }
 *     // ... 其他依赖方法
 * }
 * }</pre>
 * </p>
 *
 * @param <T>      实体类型
 * @param <TProxy> 实体代理类型
 * @param <U>      闭包实体类型
 * @param <UProxy> 闭包实体代理类型
 * @author lonbon
 * @since 1.0.0
 */
public interface ClosureOperations<T extends ProxyEntityAvailable<T, TProxy> & ClosureAvailable<U>,
        TProxy extends AbstractProxyEntity<TProxy, T>, U extends ProxyEntityAvailable<U, UProxy> & Closure,
        UProxy extends AbstractProxyEntity<UProxy, U>>
        extends ClosureDependencyProvider<T, TProxy, U, UProxy>, ClosureService<T, TProxy, U, UProxy> {

    /**
     * 闭包表字段常量
     * <p>
     * 注意：接口中的字段默认是 {@code public static final}，可省略修饰符。
     * 这些常量用于构建闭包表查询条件。
     * </p>
     */
    String CLOSURE_ANCESTOR_ID = "ancestorId";

    String CLOSURE_DESCENDANT_ID = "descendantId";

    String CLOSURE_DISTANCE = "distance";

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
    @Transactional(rollbackFor = Exception.class)
    default T createEntity(Object createDto) {
        T created = createBaseEntity(createDto);
        UUID id = created.getId();

        List<U> closures = new ArrayList<>();
        closures.add(createClosure(id, id, 0));

        UUID parentId = created.getParentId();
        if (parentId != null) {
            T parent = getEntityRepository().getById(parentId, navigate(), false).orElseThrow(
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

        Repository<U, UProxy> closureRepo = getClosureRepository();
        for (U closure : closures) {
            closureRepo.insert(closure);
        }

        return created;
    }

    /**
     * 查询直接子节点
     *
     * @param parentId 父节点ID
     * @return 直接子节点列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    default List<T> getDirectChildren(UUID parentId) {
        List<U> childClosures = getClosureRepository().getAll(u -> {
            u.anyColumn(CLOSURE_ANCESTOR_ID).eq(parentId);
            u.anyColumn(CLOSURE_DISTANCE).eq(1);
        });

        List<UUID> childIds = childClosures.stream().map(U::getDescendantId).collect(Collectors.toList());

        if (childIds.isEmpty()) {
            return List.of();
        }

        return getEntityRepository().getAllByIds(childIds);
    }

    /**
     * 查询所有后代节点（包括多级）
     *
     * @param parentId 父节点ID
     * @return 所有后代节点列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    default List<T> getDescendants(UUID parentId) {
        List<U> childClosures = getClosureRepository().getAll(u -> {
            u.anyColumn(CLOSURE_ANCESTOR_ID).eq(parentId);
            u.anyColumn(CLOSURE_DISTANCE).gt(0);
        });

        List<UUID> childIds = childClosures.stream().map(U::getDescendantId).collect(Collectors.toList());

        if (childIds.isEmpty()) {
            return List.of();
        }

        return getEntityRepository().getAllByIds(childIds);
    }

    /**
     * 查询直接父节点
     *
     * @param childId 子节点ID
     * @return 直接父节点
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    default Optional<T> getDirectParent(UUID childId) {
        List<U> parentClosures = getClosureRepository().getAll(u -> {
            u.anyColumn(CLOSURE_DESCENDANT_ID).eq(childId);
            u.anyColumn(CLOSURE_DISTANCE).eq(1);
        });

        if (parentClosures.isEmpty()) {
            return Optional.empty();
        }

        UUID parentId = parentClosures.getFirst().getAncestorId();

        return getEntityRepository().getById(parentId);
    }

    /**
     * 查询所有祖先节点
     *
     * @param childId 子节点ID
     * @return 所有祖先节点列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    default List<T> getAllAncestors(UUID childId) {
        List<U> ancestorClosures = getClosureRepository().getAll(u -> {
            u.anyColumn(CLOSURE_DESCENDANT_ID).eq(childId);
            u.anyColumn(CLOSURE_DISTANCE).gt(0);
        });

        List<UUID> ancestorIds = ancestorClosures.stream().map(U::getAncestorId).collect(Collectors.toList());

        if (ancestorIds.isEmpty()) {
            return List.of();
        }

        return getEntityRepository().getAllByIds(ancestorIds);
    }

    /**
     * 移动节点到新的父节点下
     *
     * @param nodeId      要移动的节点ID
     * @param newParentId 新的父节点ID
     * @return 移动后的节点
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    default T moveNode(UUID nodeId, UUID newParentId) {
        T node = getEntityRepository().getById(nodeId).orElseThrow(
                () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Node not found, ID: " + nodeId));

        T newParent = getEntityRepository().getById(newParentId, navigate(), false).orElseThrow(
                () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "New parent not found, ID: " + newParentId));

        if (isDescendant(nodeId, newParentId)) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Cannot move node to its own descendant");
        }

        Repository<U, UProxy> closureRepo = getClosureRepository();
        List<U> nodeClosures = closureRepo.getAll(u -> u.anyColumn(CLOSURE_DESCENDANT_ID).eq(nodeId));
        for (U closure : nodeClosures) {
            closureRepo.delete(closure);
        }

        List<U> newClosures = new ArrayList<>();
        newClosures.add(createClosure(nodeId, nodeId, 0));
        newClosures.add(createClosure(newParentId, nodeId, 1));

        List<U> parentAncestors = newParent.getAncestors();
        if (parentAncestors != null && !parentAncestors.isEmpty()) {
            for (U ancestor : parentAncestors) {
                newClosures.add(createClosure(ancestor.getAncestorId(), nodeId, ancestor.getDistance() + 1));
            }
        }

        for (U closure : newClosures) {
            closureRepo.insert(closure);
        }

        node.setParentId(newParentId);
        getEntityRepository().update(node, setColumnParentId());
        return node;
    }

    /**
     * 删除节点及其所有子节点
     *
     * @param nodeId 节点ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    default void deleteEntity(UUID nodeId) {
        Repository<U, UProxy> closureRepo = getClosureRepository();
        Repository<T, TProxy> entityRepo = getEntityRepository();

        List<U> descendantClosures = closureRepo.getAll(u -> {
            u.anyColumn(CLOSURE_ANCESTOR_ID).eq(nodeId);
            u.anyColumn(CLOSURE_DISTANCE).gt(0);
        });

        List<UUID> ids = descendantClosures.stream().map(U::getDescendantId).collect(Collectors.toList());

        ids.add(nodeId);

        List<U> allClosures = closureRepo.getAll(u -> u.or(() -> {
            u.anyColumn(CLOSURE_ANCESTOR_ID).in(ids);
            u.anyColumn(CLOSURE_DESCENDANT_ID).in(ids);
        }));

        for (U closure : allClosures) {
            closureRepo.delete(closure);
        }

        for (UUID id : ids) {
            entityRepo.deleteById(id);
        }
    }

    /**
     * 获取树状结构
     *
     * @param rootId 根节点ID
     * @return 根节点（包含子节点）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    default T getTree(UUID rootId) {
        T root = getEntityRepository().getById(rootId, navigate(), false).orElseThrow(
                () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Root node not found, ID: " + rootId));

        getSubTree(root);

        return root;
    }

    /**
     * 检查节点是否是另一个节点的后代
     *
     * @param nodeId     节点ID
     * @param ancestorId 祖先节点ID
     * @return 是否是后代
     */
    private boolean isDescendant(UUID nodeId, UUID ancestorId) {
        List<U> closures = getClosureRepository().getAll(u -> {
            u.anyColumn(CLOSURE_ANCESTOR_ID).eq(ancestorId);
            u.anyColumn(CLOSURE_DESCENDANT_ID).eq(nodeId);
        });
        return !closures.isEmpty();
    }

    /**
     * 递归获取子树
     *
     * @param parent 父节点
     */
    private void getSubTree(T parent) {

        List<T> children = getDirectChildren(parent.getId());

        for (T child : children) {
            getSubTree(child);
        }
    }
}
