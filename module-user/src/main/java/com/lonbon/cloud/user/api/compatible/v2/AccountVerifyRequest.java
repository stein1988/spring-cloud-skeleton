package com.lonbon.cloud.user.api.compatible.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "校验账号信息请求")
public class AccountVerifyRequest {

    @Schema(description = "账号类型：1000-物联设备账号，1100-对讲机设备，44-APP")
    @NotNull
    private Integer accountType;

    @Schema(description = "参数值：用户账号-用户accountId，设备账号-设备账号名")
    @NotNull
    private String accountValue;
}
