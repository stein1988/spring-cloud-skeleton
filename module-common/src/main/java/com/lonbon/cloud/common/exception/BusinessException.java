package com.lonbon.cloud.common.exception;

import com.lonbon.cloud.base.exception.BaseBusinessException;
import com.lonbon.cloud.base.exception.ErrorCode;

/**
 * 业务异常
 * 用于处理业务逻辑层面的异常
 */
public class BusinessException extends BaseBusinessException {
    
    /**
     * 构造业务异常
     * @param errorCode 错误码
     * @param errorMessage 错误消息
     */
    public BusinessException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
    
    /**
     * 构造业务异常
     * @param errorCode 错误码
     * @param errorMessage 错误消息
     * @param cause 原始异常
     */
    public BusinessException(int errorCode, String errorMessage, Throwable cause) {
        super(errorCode, errorMessage, cause);
    }
    
    /**
     * 构造业务异常
     * @param errorCode 错误码枚举
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    /**
     * 构造业务异常
     * @param errorCode 错误码枚举
     * @param cause 原始异常
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
    
    /**
     * 构造业务异常
     * @param errorCode 错误码枚举
     * @param customMessage 自定义错误消息
     */
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}