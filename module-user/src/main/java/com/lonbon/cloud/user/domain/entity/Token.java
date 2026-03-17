package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.TokenProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("lb_user_token")
@EntityProxy
public class Token extends BaseEntity implements ProxyEntityAvailable<Token, TokenProxy> {
    private String type;
    private UUID userId;
    private String accessToken;
    private OffsetDateTime accessExpireAt;
    private String refreshToken;
    private OffsetDateTime refreshExpireAt;
    private boolean isActive;
    private String deviceInfo;
    private String loginIp;
    private String scope;
}
