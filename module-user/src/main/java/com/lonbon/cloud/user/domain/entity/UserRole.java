package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.UserRoleProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

/**
 * 用户角色关联表，一个用户可以关联多个角色
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_user_role", ignoreProperties = {BaseEntity.Fields.departmentId})
@EntityProxy
public class UserRole extends BaseEntity implements ProxyEntityAvailable<UserRole, UserRoleProxy> {

    /**
     * 用户ID
     */
    private UUID userId;

    /**
     * 角色ID
     */
    private UUID roleId;
}
