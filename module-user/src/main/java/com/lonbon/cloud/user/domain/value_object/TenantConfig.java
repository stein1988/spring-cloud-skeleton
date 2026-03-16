package com.lonbon.cloud.user.domain.value_object;

import com.lonbon.cloud.base.entity.JsonObject;
import lombok.Data;

@Data
public class TenantConfig implements JsonObject {
    private int a;
    private String b;
}
