package com.lonbon.cloud.user.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequest {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码密文")
    private String password_cipher;

    @Schema(description = "签名")
    private String signature;
}
