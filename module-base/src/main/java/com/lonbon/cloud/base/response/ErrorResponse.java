package com.lonbon.cloud.base.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一错误响应类
 * 用于返回标准化的错误响应格式
 */
@Data
public class ErrorResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private Object data;

    private ErrorResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 创建错误响应
     * @param code 错误码
     * @param message 错误消息
     * @param data 附加数据
     * @return 错误响应
     */
    public static ErrorResponse of(int code, String message, Object data) {
        return new ErrorResponse(code, message, data);
    }

    /**
     * 创建错误响应
     * @param code 错误码
     * @param message 错误消息
     * @return 错误响应
     */
    public static ErrorResponse of(int code, String message) {
        return new ErrorResponse(code, message, null);
    }

    /**
     * 创建错误响应
     * @param message 错误消息
     * @return 错误响应
     */
    public static ErrorResponse of(String message) {
        return new ErrorResponse(50000, message, null);
    }
}