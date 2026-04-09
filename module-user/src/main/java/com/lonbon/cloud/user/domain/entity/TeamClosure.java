package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.TeamClosureProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * 团队闭包表，表达团队之间的层级关系
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_team_closure", ignoreProperties = {"updateTime", "updateBy", "version"})
@EntityProxy
public class TeamClosure extends BaseEntity implements ProxyEntityAvailable<TeamClosure, TeamClosureProxy> {
    
    /**
     * 租户ID
     */
    private UUID tenantId;
    
    /**
     * 祖先团队ID
     */
    private UUID ancestorId;
    
    /**
     * 后代团队ID
     */
    private UUID descendantId;
    
    /**
     * 层级距离，取值>=0，0表示自身，ancestorId=descendantId，1表示直接后代
     */
    private int distance;
}
