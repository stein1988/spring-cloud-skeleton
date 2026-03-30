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
@EntityProxy(ignoreProperties = "config")
public class Tenant extends BaseEntity implements ProxyEntityAvailable<Tenant , TenantProxy> {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 域名
     */
    private String domain;
    private boolean isDefault;
    private boolean isActive;

    private TenantConfig config;
    private boolean isSystem;
}
