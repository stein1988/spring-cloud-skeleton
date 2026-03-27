package com.lonbon.cloud.user.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @Schema(description = "刷新令牌")
    private String refreshToken;
}
