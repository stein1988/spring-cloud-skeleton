package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.base.service.ClosureEntity;
import com.lonbon.cloud.user.domain.entity.proxy.TenantClosureProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 租户闭包表，表达租户之间的层级关系
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(value = "sys_tenant_closure", ignoreProperties = {BaseEntity.Fields.tenantId, BaseEntity.Fields.departmentId})
@EntityProxy
public class TenantClosure extends ClosureEntity implements ProxyEntityAvailable<TenantClosure, TenantClosureProxy> {

    public TenantClosure(UUID ancestorId, UUID descendantId, Integer distance) {
        super(ancestorId, descendantId, distance);
    }
}
