package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.UserTeamProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("lb_user_team")
@EntityProxy
public class UserTeam extends BaseEntity implements ProxyEntityAvailable<UserTeam, UserTeamProxy> {
    private UUID tenantId;
    private UUID userId;
    private UUID teamId;
    private boolean isActive;
}
