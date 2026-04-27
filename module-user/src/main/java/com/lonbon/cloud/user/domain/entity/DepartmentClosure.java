package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.base.service.ClosureEntity;
import com.lonbon.cloud.user.domain.entity.proxy.DepartmentClosureProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 部门闭包表，表达部门之间的层级关系
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(value = "sys_department_closure", ignoreProperties = {BaseEntity.Fields.departmentId})
@EntityProxy
public class DepartmentClosure extends ClosureEntity
        implements ProxyEntityAvailable<DepartmentClosure, DepartmentClosureProxy> {

    public DepartmentClosure(UUID ancestorId, UUID descendantId, Integer distance) {
        super(ancestorId, descendantId, distance);
    }
}
