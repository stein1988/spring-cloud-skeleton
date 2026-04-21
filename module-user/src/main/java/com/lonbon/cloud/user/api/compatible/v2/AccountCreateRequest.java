package com.lonbon.cloud.user.api.compatible.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "账号注册请求")
public class AccountCreateRequest {

    @Schema(description = "账号名称，设备类型为设备唯一标识")
    @NotNull
    private String username;

    @Schema(description = "账号密码（APP类型需要）")
    private String password;

    @Schema(description = "账号类型：1000-物联设备类型，1100-对讲机设备类型，44-馨刻APP")
    @NotNull
    private Integer accountType;

    @Schema(description = "机构ID")
    private String orgId;

    @Schema(description = "设备附加信息（设备类型必填）")
    private DeviceObjVO deviceObj;
}
