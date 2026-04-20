package com.lonbon.cloud.base.satoken;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserAccessToken {

    private UUID userId;

    private String token;

    /**
     * 有效期（单位: 秒）
     */
    private long timeout;

    public static UserAccessToken generate(UUID userId, List<String> roles) {
        StpUtil.login(userId.toString(),
                      new SaLoginParameter().setExtra(JWTUtil.SUBJECT, userId).setExtra(JWTUtil.ROLES, roles));
        SaTokenInfo info = StpUtil.getTokenInfo();
        UserAccessToken accessToken = new UserAccessToken();
        accessToken.userId = userId;
        accessToken.token = info.getTokenValue();
        accessToken.timeout = info.getTokenTimeout();
        return accessToken;
    }
}
