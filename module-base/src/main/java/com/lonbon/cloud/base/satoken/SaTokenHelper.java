package com.lonbon.cloud.base.satoken;

import cn.dev33.satoken.stp.StpUtil;

import java.util.UUID;

public class SaTokenHelper {

    public static final UUID NULL_UUID = new UUID(0, 0);

    public static UUID getLoginId() {
        Object loginId = StpUtil.getLoginId();
        if (loginId instanceof String s) {
            return UUID.fromString(s);
        } else {
            return NULL_UUID;
        }
    }
}
