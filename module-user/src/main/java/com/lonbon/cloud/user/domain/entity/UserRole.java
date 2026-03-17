package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.UserRoleProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("lb_user_role")
@EntityProxy
public class UserRole extends BaseEntity implements ProxyEntityAvailable<UserRole, UserRoleProxy> {
    private UUID tenantId;
    private UUID teamId;
    private UUID userId;
    private UUID roleId;
}
