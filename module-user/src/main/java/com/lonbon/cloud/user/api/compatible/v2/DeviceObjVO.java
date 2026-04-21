package com.lonbon.cloud.user.api.compatible.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "设备附加信息")
public class DeviceObjVO {

    @Schema(description = "设备类别码，详见设备分类枚举")
    private Integer deviceCategory;
}
