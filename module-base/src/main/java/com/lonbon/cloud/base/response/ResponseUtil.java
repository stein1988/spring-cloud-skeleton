package com.lonbon.cloud.base.response;

import com.lonbon.cloud.base.exception.ErrorCode;

/**
 * 响应工具类
 * 用于快速构建异常响应
 */
public class ResponseUtil {

    /**
     * 构建错误响应
     * @param code 错误码
     * @param message 错误消息
     * @param data 附加数据
     * @return 错误响应
     */
    public static ErrorResponse error(int code, String message, Object data) {
        return ErrorResponse.of(code, message, data);
    }

    /**
     * 构建错误响应
     * @param code 错误码
     * @param message 错误消息
     * @return 错误响应
     */
    public static ErrorResponse error(int code, String message) {
        return ErrorResponse.of(code, message);
    }

    /**
     * 构建错误响应
     * @param message 错误消息
     * @return 错误响应
     */
    public static ErrorResponse error(String message) {
        return ErrorResponse.of(message);
    }

    /**
     * 构建错误响应
     * @param errorCode 错误码枚举
     * @param data 附加数据
     * @return 错误响应
     */
    public static ErrorResponse error(ErrorCode errorCode, Object data) {
        return ErrorResponse.of(errorCode.getCode(), errorCode.getMessage(), data);
    }

    /**
     * 构建错误响应
     * @param errorCode 错误码枚举
     * @return 错误响应
     */
    public static ErrorResponse error(ErrorCode errorCode) {
        return ErrorResponse.of(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 构建错误响应
     * @param errorCode 错误码枚举
     * @param customMessage 自定义错误消息
     * @return 错误响应
     */
    public static ErrorResponse error(ErrorCode errorCode, String customMessage) {
        return ErrorResponse.of(errorCode.getCode(), customMessage);
    }
}