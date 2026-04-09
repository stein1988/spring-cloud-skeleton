package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.UserTeamProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * 用户团队关联表，一个用户可以关联一个租户下的多个团队
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_user_team")
@EntityProxy
public class UserTeam extends BaseEntity implements ProxyEntityAvailable<UserTeam, UserTeamProxy> {
    
    /**
     * 用户ID
     */
    private UUID userId;
    
    /**
     * 团队ID
     */
    private UUID teamId;
    
    /**
     * 是否激活
     */
    private boolean isActive;
    
    /**
     * 是否团队管理员
     */
    private boolean isTeamAdmin;
}
