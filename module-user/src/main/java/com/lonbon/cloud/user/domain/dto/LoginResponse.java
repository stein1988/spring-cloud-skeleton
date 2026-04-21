package com.lonbon.cloud.user.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor  // 全参构造
@NoArgsConstructor   // 无参构造（在有全参构造的情况下，必须手动加，否则会丢失无参构造）
public class LoginResponse {

    @Schema(description = "用户ID")
    private UUID userId;

    @Schema(description = "访问令牌")
    private String accessToken;

    @Schema(description = "访问令牌过期时间(秒)")
    private long accessExpireIn;

    @Schema(description = "刷新令牌")
    private String refreshToken;

    @Schema(description = "刷新令牌过期时间(秒)")
    private long refreshExpireIn;
}
