package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.TeamProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("lb_team")
@EntityProxy
public class Team extends BaseEntity implements ProxyEntityAvailable<Team , TeamProxy> {
    private UUID tenantId;
    private String name;
    private String description;
    private boolean isDefault;
    private boolean isActive;
    private String logoUrl;
    private String config;
    private boolean isSystem;
}
