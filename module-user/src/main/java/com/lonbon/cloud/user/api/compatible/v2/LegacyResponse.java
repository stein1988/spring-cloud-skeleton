package com.lonbon.cloud.user.api.compatible.v2;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LegacyResponse<T> {

    private String status;

    private String msg;

    private T body;

    public static <T> LegacyResponse<T> success(String status, String msg, T body) {
        return new LegacyResponse<T>().setStatus(status).setMsg(msg).setBody(body);
    }

    public static <T> LegacyResponse<T> success(String msg, T body) {
        return success("200", msg, body);
    }

    public static <T> LegacyResponse<T> success(T body) {
        return success("200", "成功", body);
    }

    public static <T> LegacyResponse<T> success() {
        return success("200", "成功", null);
    }

    public static <T> LegacyResponse<T> error(String status, String msg, T body) {
        return new LegacyResponse<T>().setStatus(status).setMsg(msg).setBody(body);
    }

    public static <T> LegacyResponse<T> error(String status, String msg) {
        return error(status, msg, null);
    }

    public static <T> LegacyResponse<T> error(String msg) {
        return error("201", msg, null);
    }
}
