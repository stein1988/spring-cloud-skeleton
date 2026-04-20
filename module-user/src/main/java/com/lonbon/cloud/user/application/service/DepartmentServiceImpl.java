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
 * 部门层级关系通过闭包表（DepartmentClosure）实现，支持多级部门结构。
 * </p>
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepartmentServiceImpl extends ClosureEntityService<Department, DepartmentProxy, DepartmentClosure, DepartmentClosureProxy>
        implements DepartmentService {
    public DepartmentServiceImpl(
            Converter converter, DepartmentRepository repository, DepartmentClosureRepository closureRepository) {
        super(converter, repository, closureRepository, Department.class);
    }

    @Override
    protected SQLActionExpression2<IncludeContext, DepartmentProxy> navigate() {
        return (c, t) -> c.query(t.ancestors());
    }

    @Override
    protected SQLFuncExpression1<DepartmentProxy, SQLSelectExpression> setColumnParentId() {
        return DepartmentProxy::parentId;
    }


    @Override
    protected DepartmentClosure createClosure(UUID ancestorId, UUID descendantId, Integer distance) {
        return new DepartmentClosure(ancestorId, descendantId, distance);
    }
}
