package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.UserProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("lb_user")
@EntityProxy
public class User extends BaseEntity implements ProxyEntityAvailable<User , UserProxy> {
    private String username;
    private String passwordHash;
    private String passwordSalt;
    private UUID currentTenantId;
    private UUID currentTeamId;
    private boolean isSuperAdmin;
    private boolean isActive;
    private LocalDateTime lastLoginAt;
    private String lastLoginIp;
    private String name;
    private String email;
    private String phone;
    private String avatarUrl;
    private String timezone;
    private String language;
    private boolean isSystem;
}
