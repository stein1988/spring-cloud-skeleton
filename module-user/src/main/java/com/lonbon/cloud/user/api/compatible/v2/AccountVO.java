package com.lonbon.cloud.user.api.compatible.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "账号信息")
public class AccountVO {

    @Schema(description = "账号唯一标识")
    private String accountId;

    @Schema(description = "用户名/设备名")
    private String username;

    @Schema(description = "账号类型")
    private Integer accountType;
}
