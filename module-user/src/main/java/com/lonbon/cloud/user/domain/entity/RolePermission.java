package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.RolePermissionProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

/**
 * 角色权限关联表实体类
 * <p>
 * 角色权限关联表用于存储角色与权限之间的关联关系，一个角色可以关联多个权限。
 * 每个关联记录包含角色ID、权限ID等信息。
 * </p>
 */
@Data
@FieldNameConstants
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
