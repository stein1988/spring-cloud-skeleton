package com.lonbon.cloud.base.exception;

/**
 * 基础业务异常类
 * 所有业务异常都应继承此类
 */
public class BaseBusinessException extends RuntimeException {
    private final int errorCode;
    private final String errorMessage;
    
    /**
     * 构造基础业务异常
     * @param errorCode 错误码
     * @param errorMessage 错误消息
     */
    public BaseBusinessException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    /**
     * 构造基础业务异常
     * @param errorCode 错误码
     * @param errorMessage 错误消息
     * @param cause 原始异常
     */
    public BaseBusinessException(int errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    /**
     * 构造基础业务异常
     * @param errorCode 错误码枚举
     */
    public BaseBusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorCode.getMessage();
    }
    
    /**
     * 构造基础业务异常
     * @param errorCode 错误码枚举
     * @param cause 原始异常
     */
    public BaseBusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorCode.getMessage();
    }
    
    /**
     * 构造基础业务异常
     * @param errorCode 错误码枚举
     * @param customMessage 自定义错误消息
     */
    public BaseBusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode.getCode();
        this.errorMessage = customMessage;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}