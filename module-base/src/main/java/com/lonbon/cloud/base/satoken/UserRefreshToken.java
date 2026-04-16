package com.lonbon.cloud.base.satoken;

import cn.dev33.satoken.temp.SaTempUtil;
import cn.hutool.jwt.JWT;
import lombok.Data;

import java.util.UUID;

import static cn.dev33.satoken.jwt.SaJwtTemplate.NEVER_EXPIRE;

@Data
public class UserRefreshToken {

    private UUID userId;

    private String accessToken;

    private String refreshToken;

    /**
     * 有效时间，单位：秒，-1 代表永久有效
     */
    private long timeout;

    private long expirationTime;
    
    public static UserRefreshToken generate(UUID userId, String accessToken, long timeout) {
        long expirationTime = timeout > 0 ? timeout * 1000 + System.currentTimeMillis() : NEVER_EXPIRE;
        UserRefreshToken refreshToken = new UserRefreshToken();
        refreshToken.userId = userId;
        refreshToken.accessToken = accessToken;
        refreshToken.expirationTime = expirationTime;
        refreshToken.refreshToken = SaTempUtil.createToken(refreshToken, timeout);
        return refreshToken;
    }

    public static UserRefreshToken parse(String refreshToken) {
        // TODO：增加jwt token的自校验
        Object value = SaTempUtil.parseToken(refreshToken);
        if (value instanceof UserRefreshToken token) {
            return token;
        }
        return null;
    }

    public JWT getJwt() {
        String sub = userId.toString().replace("-", "");

        return JWT.create().setJWTId(JWTUtil.generateJti()).setSubject(sub)
                  .setPayload(JWTUtil.EXPIRATION_TIME, expirationTime);
    }
}
