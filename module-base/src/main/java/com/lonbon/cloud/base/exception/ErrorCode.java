package com.lonbon.cloud.base.exception;

/**
 * 错误码定义
 * 遵循 REST 风格，使用 HTTP 状态码作为基础
 */
public enum ErrorCode {
    // 系统错误 (500xx)
    SYSTEM_ERROR(50000, "系统内部错误"),
    DATABASE_ERROR(50001, "数据库操作错误"),
    NETWORK_ERROR(50002, "网络请求错误"),
    SERVICE_UNAVAILABLE(50003, "服务不可用"),
    
    // 业务错误 (400xx)
    BUSINESS_ERROR(40000, "业务逻辑错误"),
    RESOURCE_NOT_FOUND(40001, "资源不存在"),
    RESOURCE_ALREADY_EXISTS(40002, "资源已存在"),
    OPERATION_NOT_ALLOWED(40003, "操作不允许"),
    PERMISSION_DENIED(40004, "权限不足"),
    
    // 参数错误 (400xx)
    PARAMETER_ERROR(40005, "参数错误"),
    MISSING_PARAMETER(40006, "缺少必要参数"),
    INVALID_PARAMETER(40007, "参数值无效"),
    PARAMETER_TOO_LONG(40008, "参数长度过长"),
    
    // 认证错误 (401xx)
    UNAUTHORIZED(40100, "未授权"),
    INVALID_TOKEN(40101, "无效的令牌"),
    TOKEN_EXPIRED(40102, "令牌已过期"),
    LOGIN_REQUIRED(40103, "需要登录"),
    
    // 授权错误 (403xx)
    FORBIDDEN(40300, "禁止访问"),
    ACCESS_DENIED(40301, "访问被拒绝"),
    
    // 请求错误 (404xx)
    NOT_FOUND(40400, "请求的资源不存在"),
    METHOD_NOT_ALLOWED(40401, "请求方法不允许"),
    
    // 其他错误
    UNKNOWN_ERROR(99999, "未知错误");
    
    private final int code;
    private final String message;
    
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    /**
     * 根据错误码获取错误枚举
     * @param code 错误码
     * @return 错误枚举
     */
    public static ErrorCode getByCode(int code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.code == code) {
                return errorCode;
            }
        }
        return UNKNOWN_ERROR;
    }
}