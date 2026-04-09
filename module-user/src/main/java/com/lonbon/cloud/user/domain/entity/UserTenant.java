package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.UserTenantProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * 用户租户关联表，一个用户可以关联多个租户
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_user_tenant", ignoreProperties = {"departmentId"})
@EntityProxy
public class UserTenant extends BaseEntity implements ProxyEntityAvailable<UserTenant, UserTenantProxy> {

    /**
     * 用户ID
     */
    private UUID userId;

    /**
     * 租户ID在BaseEntity中定义
     */

    /**
     * 是否激活
     */
    private boolean isActive;

    /**
     * 是否租户管理员
     */
    private boolean isTenantAdmin;
}
