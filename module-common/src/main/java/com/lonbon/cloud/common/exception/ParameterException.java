package com.lonbon.cloud.common.exception;

import com.lonbon.cloud.base.exception.BaseBusinessException;
import com.lonbon.cloud.base.exception.ErrorCode;

/**
 * 参数异常
 * 用于处理参数验证失败等情况
 */
public class ParameterException extends BaseBusinessException {
    
    /**
     * 构造参数异常
     * @param errorCode 错误码
     * @param errorMessage 错误消息
     */
    public ParameterException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
    
    /**
     * 构造参数异常
     * @param errorCode 错误码
     * @param errorMessage 错误消息
     * @param cause 原始异常
     */
    public ParameterException(int errorCode, String errorMessage, Throwable cause) {
        super(errorCode, errorMessage, cause);
    }
    
    /**
     * 构造参数异常
     * @param errorCode 错误码枚举
     */
    public ParameterException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    /**
     * 构造参数异常
     * @param errorCode 错误码枚举
     * @param cause 原始异常
     */
    public ParameterException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
    
    /**
     * 构造参数异常
     * @param errorCode 错误码枚举
     * @param customMessage 自定义错误消息
     */
    public ParameterException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}