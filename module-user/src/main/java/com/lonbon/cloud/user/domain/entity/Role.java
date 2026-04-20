package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.RoleProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.util.List;

/**
 * 角色
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_role", ignoreProperties = {BaseEntity.Fields.departmentId})
@EntityProxy
public class Role extends BaseEntity implements ProxyEntityAvailable<Role, RoleProxy> {

    public static final String SUPER_ADMIN = "super_admin";
    public static final String TENANT_ADMIN = "tenant_admin";

    /**
     * 类型，暂时无用
     */
    private String type;

    /**
     * 代号
     */
    private String code;

    /**
     * 显示名称，例如："管理员"
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    @Navigate(value = RelationTypeEnum.ManyToMany, mappingClass = UserRole.class, selfProperty = BaseEntity.Fields.id
            , selfMappingProperty = UserRole.Fields.roleId, targetMappingProperty = UserRole.Fields.userId,
            targetProperty = BaseEntity.Fields.id, subQueryToGroupJoin = true)
    private List<User> users;

    @Navigate(value = RelationTypeEnum.OneToMany, selfProperty = BaseEntity.Fields.id, targetProperty =
            UserRole.Fields.roleId)
    private List<UserRole> userRoles;
}
