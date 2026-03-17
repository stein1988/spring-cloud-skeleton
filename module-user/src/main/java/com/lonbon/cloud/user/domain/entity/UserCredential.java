package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.UserCredentialProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("lb_user_credential")
@EntityProxy
public class UserCredential extends BaseEntity implements ProxyEntityAvailable<UserCredential, UserCredentialProxy> {
    private UUID userId;
    private String type;
    private String openId;
    private UUID oauthResourceId;
    private Map<String, Object> userInfo;
}
