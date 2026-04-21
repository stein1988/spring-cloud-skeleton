package com.lonbon.cloud.user.api.compatible.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "账号登录请求")
public class AuthLoginRequest {

    @Schema(description = "账号信息")
    @NotNull
    private String userName;

    @Schema(description = "密码（APP类型需要）")
    private String password;

    @Schema(description = "账号类型：1000-设备类型，1100-对讲机类型，44-馨刻APP")
    @NotNull
    private Integer accountType;

    @Schema(description = "机构ID，默认为空字符串")
    private String orgId;
}
