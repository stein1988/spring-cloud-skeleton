package com.lonbon.cloud.user.application.service;

import com.lonbon.cloud.base.service.ClosureExtension;
import com.lonbon.cloud.base.service.ClosureOperation;
import com.lonbon.cloud.base.service.EntityServiceImpl;
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
 * 部门层级关系通过闭包表实现，支持多级部门结构。
 * </p>
 *
 * @author lonbon
 * @see ClosureExtension
 * @see DepartmentService
 * @since 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepartmentServiceImpl extends EntityServiceImpl<Department, DepartmentProxy> implements DepartmentService {

    private final ClosureExtension<Department, DepartmentProxy, DepartmentClosure, DepartmentClosureProxy> closureExtension;

    public DepartmentServiceImpl(
            Converter converter, DepartmentRepository repository, DepartmentClosureRepository closureRepository) {
        super(converter, repository, Department.class);
        this.closureExtension = new ClosureExtension<>(repository, closureRepository, (c, t) -> c.query(t.ancestors()),
                                                       DepartmentProxy::parentId) {
            @Override
            protected DepartmentClosure createClosure(UUID ancestorId, UUID descendantId, Integer distance) {
                return new DepartmentClosure(ancestorId, descendantId, distance);
            }
        };
    }

    @Override
    public ClosureOperation<Department, DepartmentProxy, DepartmentClosure, DepartmentClosureProxy> getClosureOperation() {
        return closureExtension;
    }
}
