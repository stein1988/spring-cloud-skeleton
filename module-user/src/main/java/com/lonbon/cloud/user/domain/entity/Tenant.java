package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.TenantProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("lb_tenant")
@EntityProxy
public class Tenant extends BaseEntity implements ProxyEntityAvailable<Tenant , TenantProxy> {
    private String name;
    private String description;
    private boolean isDefault;
    private boolean isActive;
    private String domain;
    private String config;
    private boolean isSystem;
}
