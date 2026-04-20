package com.lonbon.cloud.user.application.service;

import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.expression.lambda.SQLFuncExpression1;
import com.easy.query.core.proxy.SQLSelectExpression;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.service.ClosureEntityService;
import com.lonbon.cloud.user.domain.entity.Department;
import com.lonbon.cloud.user.domain.entity.DepartmentClosure;
import com.lonbon.cloud.user.domain.entity.proxy.DepartmentClosureProxy;
import com.lonbon.cloud.user.domain.entity.proxy.DepartmentProxy;
import com.lonbon.cloud.user.domain.repository.DepartmentClosureRepository;
import com.lonbon.cloud.user.domain.repository.DepartmentRepository;
import com.lonbon.cloud.user.domain.service.DepartmentService;
import io.github.linpeilie.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 部门服务实现类
 * <p>
 * 提供部门的增删改查及层级关系管理功能。
 * 部门层级关系通过闭包表（{@link DepartmentClosure}）实现，支持多级部门结构。
 * </p>
 *
 * @author lonbon
 * @since 1.0.0
 * @see ClosureEntityService
 * @see DepartmentService
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepartmentServiceImpl extends ClosureEntityService<Department, DepartmentProxy, DepartmentClosure, DepartmentClosureProxy>
        implements DepartmentService {
    
    /**
     * 构造部门服务实现
     *
     * @param converter          DTO转换器
     * @param repository         部门仓库
     * @param closureRepository  部门闭包表仓库
     */
    public DepartmentServiceImpl(
            Converter converter, DepartmentRepository repository, DepartmentClosureRepository closureRepository) {
        super(converter, repository, closureRepository, Department.class);
    }

    /**
     * 获取导航属性表达式
     * <p>
     * 配置部门实体的导航属性，用于关联查询祖先节点。
     * </p>
     *
     * @return 导航属性表达式
     */
    @Override
    protected SQLActionExpression2<IncludeContext, DepartmentProxy> navigate() {
        return (c, t) -> c.query(t.ancestors());
    }

    /**
     * 设置父ID列的更新表达式
     * <p>
     * 定义更新父ID字段的表达式，用于移动节点操作。
     * </p>
     *
     * @return 父ID列更新表达式
     */
    @Override
    protected SQLFuncExpression1<DepartmentProxy, SQLSelectExpression> setColumnParentId() {
        return DepartmentProxy::parentId;
    }


    /**
     * 创建闭包记录
     * <p>
     * 用于在创建部门或移动部门时，创建闭包表记录。
     * </p>
     *
     * @param ancestorId   祖先节点ID
     * @param descendantId 后代节点ID
     * @param distance     距离（层级差）
     * @return 部门闭包记录
     */
    @Override
    protected DepartmentClosure createClosure(UUID ancestorId, UUID descendantId, Integer distance) {
        return new DepartmentClosure(ancestorId, descendantId, distance);
    }
}
