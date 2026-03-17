package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.TenantUserProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("lb_user_tenant")
@EntityProxy
public class TenantUser extends BaseEntity implements ProxyEntityAvailable<TenantUser, TenantUserProxy> {
    private UUID userId;
    private UUID tenantId;
    private boolean isActive;
    private boolean isTenantAdmin;
}
