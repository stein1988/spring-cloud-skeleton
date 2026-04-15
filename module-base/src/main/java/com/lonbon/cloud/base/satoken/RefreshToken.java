package com.lonbon.cloud.base.satoken;

import cn.dev33.satoken.temp.SaTempUtil;
import cn.hutool.jwt.JWT;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor  // 全参构造
@NoArgsConstructor
public class RefreshToken {

    private UUID userId;

    private String accessToken;

    private long expiresIn;

    public static RefreshToken parse(String refreshToken) {
        // TODO：增加jwt token的自校验
        Object value = SaTempUtil.parseToken(refreshToken);
        if (value instanceof RefreshToken token) {
            return token;
        }
        return null;
    }

    public JWT getJwt() {
        String sub = userId.toString().replace("-", "");

        return JWT.create().setJWTId(JWTKey.generateJti()).setSubject(sub)
                  .setPayload(JWTKey.EXPIRATION_TIME, expiresIn);
    }

    public String generate() {
        return SaTempUtil.createToken(this, expiresIn);
    }
}
