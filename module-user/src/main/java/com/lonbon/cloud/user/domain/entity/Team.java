package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.TeamProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * 团队
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_team")
@EntityProxy
public class Team extends BaseEntity implements ProxyEntityAvailable<Team, TeamProxy> {

    /**
     * 租户ID
     */
    private UUID tenantId;

    /**
     * 类型
     */
    private String type;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否默认团队
     */
    private boolean isDefault;

    /**
     * 是否激活
     */
    private boolean isActive;
}
