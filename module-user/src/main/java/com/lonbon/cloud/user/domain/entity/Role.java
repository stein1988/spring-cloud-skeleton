package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.RoleProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

/**
 * 角色
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Table("sys_role")
@EntityProxy
public class Role extends BaseEntity implements ProxyEntityAvailable<Role, RoleProxy> {

    /**
     * 租户ID
     */
    private UUID tenantId;

    /**
     * 团队ID
     */
    private UUID teamId;

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
}
