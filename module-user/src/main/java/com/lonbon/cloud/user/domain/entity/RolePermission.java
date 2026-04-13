package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.RolePermissionProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * 角色权限关联表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_role_permission", ignoreProperties = {BaseEntity.Fields.departmentId})
@EntityProxy
public class RolePermission extends BaseEntity implements ProxyEntityAvailable<RolePermission, RolePermissionProxy> {
    /**
     * 角色ID
     */
    private UUID roleId;

    /**
     * 权限ID
     */
    private UUID permissionId;
}
