package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.RoleProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_role")
@EntityProxy
public class Role extends BaseEntity implements ProxyEntityAvailable<Role, RoleProxy> {
    private UUID tenantId;
    private UUID teamId;
    private String type;
    private String name;
    private String description;
}
