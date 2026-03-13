package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.TenantProxy;
import com.lonbon.cloud.user.domain.value_object.TenantConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_tenant")
@EntityProxy
public class Tenant extends BaseEntity implements ProxyEntityAvailable<Tenant , TenantProxy> {
    private String name;
    private String description;
    private boolean isDefault;
    private boolean isActive;
    private String domain;
    private TenantConfig config;
    private boolean isSystem;
}
