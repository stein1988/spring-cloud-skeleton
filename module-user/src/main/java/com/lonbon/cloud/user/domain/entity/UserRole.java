package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.UserRoleProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * 用户角色关联表，一个用户可以关联多个角色
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_user_role")
@EntityProxy
public class UserRole extends BaseEntity implements ProxyEntityAvailable<UserRole, UserRoleProxy> {
    private UUID tenantId;
    private UUID teamId;
    private UUID userId;
    private UUID roleId;
}
