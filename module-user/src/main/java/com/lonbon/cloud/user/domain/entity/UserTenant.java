package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.UserTenantProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_user_tenant")
@EntityProxy
public class UserTenant extends BaseEntity implements ProxyEntityAvailable<UserTenant, UserTenantProxy> {
    private UUID userId;
    private UUID tenantId;
    private boolean isActive;
    private boolean isTenantAdmin;
}
