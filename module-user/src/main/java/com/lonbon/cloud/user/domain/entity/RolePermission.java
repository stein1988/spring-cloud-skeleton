package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.RolePermissionProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("lb_role_permission")
@EntityProxy
public class RolePermission extends BaseEntity implements ProxyEntityAvailable<RolePermission, RolePermissionProxy> {
    private UUID tenantId;
    private UUID teamId;
    private UUID roleId;
    private UUID permissionId;
}
