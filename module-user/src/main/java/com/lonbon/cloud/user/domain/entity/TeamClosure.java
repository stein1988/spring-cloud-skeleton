package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.TeamClosureProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_team_closure")
@EntityProxy
public class TeamClosure extends BaseEntity implements ProxyEntityAvailable<TeamClosure, TeamClosureProxy> {
    private UUID tenantId;
    private UUID ancestorId;
    private UUID descendantId;
    private int distance;
}
