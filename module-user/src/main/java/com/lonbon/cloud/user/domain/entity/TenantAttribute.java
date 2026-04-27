package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.base.service.AttributeEntity;
import com.lonbon.cloud.user.domain.entity.proxy.TenantAttributeProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_tenant_attribute", ignoreProperties = {BaseEntity.Fields.tenantId, BaseEntity.Fields.departmentId})
@EntityProxy
public class TenantAttribute extends AttributeEntity
        implements ProxyEntityAvailable<TenantAttribute, TenantAttributeProxy> {
}
