package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.base.service.Closure;
import com.lonbon.cloud.user.domain.entity.proxy.TenantClosureProxy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

/**
 * 租户闭包表，表达租户之间的层级关系
 */
@Data
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_tenant_closure", ignoreProperties = {BaseEntity.Fields.tenantId, BaseEntity.Fields.departmentId})
@EntityProxy
public class TenantClosure extends BaseEntity
        implements ProxyEntityAvailable<TenantClosure, TenantClosureProxy>, Closure {

    /**
     * 祖先租户ID
     */
    private UUID ancestorId;

    /**
     * 后代租户ID
     */
    private UUID descendantId;

    /**
     * 层级距离，取值>=0，不能为负数，0表示自身，ancestorId=descendantId，1表示直接后代
     */
    private Integer distance;
}
