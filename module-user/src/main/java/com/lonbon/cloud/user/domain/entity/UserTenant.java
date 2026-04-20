package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.UserTenantProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

/**
 * 用户租户关联表实体类
 * <p>
 * 用户租户关联表用于存储用户与租户之间的关联关系，一个用户可以关联多个租户。
 * 每个关联记录包含用户ID、租户ID、是否激活和是否租户管理员等信息。
 * </p>
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_user_tenant", ignoreProperties = {BaseEntity.Fields.departmentId})
@EntityProxy
public class UserTenant extends BaseEntity implements ProxyEntityAvailable<UserTenant, UserTenantProxy> {

    /**
     * 用户ID
     */
    private UUID userId;

    /*
      租户ID在BaseEntity中定义
     */

    /**
     * 是否激活
     */
    private Boolean isActive;

    /**
     * 是否租户管理员
     */
    private Boolean isTenantAdmin;
}
