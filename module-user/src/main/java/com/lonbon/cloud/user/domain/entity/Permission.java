package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.PermissionProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("lb_permission")
@EntityProxy
public class Permission extends BaseEntity implements ProxyEntityAvailable<Permission, PermissionProxy> {
    private UUID tenantId;
    private UUID teamId;
    private String type;
    private String name;
    private String description;
    private String resource;
    private String action;
}
