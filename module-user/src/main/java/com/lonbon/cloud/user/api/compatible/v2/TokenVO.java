package com.lonbon.cloud.user.api.compatible.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "Token信息")
public class TokenVO {

    @Schema(description = "访问令牌（RSA加密）")
    private String accessToken;

    @Schema(description = "过期时间（秒），设备Token有效期90天")
    private Long expireTime;

    @Schema(description = "刷新令牌（RSA加密）")
    private String refreshToken;
}
