package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.PermissionProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限
 * <p>
 * 权限实体类，用于存储系统中的权限信息。
 * 权限是系统中对用户操作进行限制的一种机制，通过定义不同的权限可以控制用户对系统资源的访问权限。
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_permission", ignoreProperties = {BaseEntity.Fields.departmentId})
@EntityProxy
public class Permission extends BaseEntity implements ProxyEntityAvailable<Permission, PermissionProxy> {
    /**
     * 类型，暂时无用
     */
    private String type;

    /**
     * 显示名称，例如："创建用户"
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 系统内部代号，例如："sys:user:add"
     */
    private String code;

    /**
     * 资源，暂时无用
     */
    private String resource;

    /**
     * 操作，暂时无用
     */
    private String action;
}
