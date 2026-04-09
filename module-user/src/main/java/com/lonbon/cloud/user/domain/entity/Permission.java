package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.PermissionProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * 权限
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_permission")
@EntityProxy
public class Permission extends BaseEntity implements ProxyEntityAvailable<Permission, PermissionProxy> {
    
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
    
    /**
     * 资源
     */
    private String resource;
    
    /**
     * 操作
     */
    private String action;
}
