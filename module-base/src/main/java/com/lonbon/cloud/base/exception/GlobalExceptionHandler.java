package com.lonbon.cloud.base.exception;

import com.lonbon.cloud.base.response.ErrorResponse;
import com.lonbon.cloud.base.response.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 处理所有控制器抛出的异常，返回统一格式的错误响应
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理基础业务异常
     * @param e 基础业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BaseBusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBaseBusinessException(BaseBusinessException e) {
        log.warn("BaseBusinessException occurred: {}", e.getMessage(), e);
        return ResponseUtil.error(e.getErrorCode(), e.getErrorMessage());
    }
    
    /**
     * 处理基础技术异常
     * @param e 基础技术异常
     * @return 错误响应
     */
    @ExceptionHandler(BaseTechException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleBaseTechException(BaseTechException e) {
        log.error("BaseTechException occurred: {}", e.getMessage(), e);
        return ResponseUtil.error(e.getErrorCode(), e.getErrorMessage());
    }
    
    /**
     * 处理参数验证异常
     * @param e 参数验证异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("MethodArgumentNotValidException occurred: {}", errorMessage, e);
        return ResponseUtil.error(ErrorCode.PARAMETER_ERROR.getCode(), errorMessage);
    }
    
    /**
     * 处理请求参数绑定异常
     * @param e 请求参数绑定异常
     * @return 错误响应
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBindException(BindException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("BindException occurred: {}", errorMessage, e);
        return ResponseUtil.error(ErrorCode.PARAMETER_ERROR.getCode(), errorMessage);
    }
    
    /**
     * 处理路径参数转换异常
     * @param e 路径参数转换异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String errorMessage = String.format("参数 %s 类型错误，期望类型: %s", e.getName(), e.getRequiredType().getSimpleName());
        log.warn("MethodArgumentTypeMismatchException occurred: {}", errorMessage, e);
        return ResponseUtil.error(ErrorCode.PARAMETER_ERROR.getCode(), errorMessage);
    }
    
    /**
     * 处理资源不存在异常
     * @param e 资源不存在异常
     * @return 错误响应
     */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchElementException(NoSuchElementException e) {
        log.warn("NoSuchElementException occurred: {}", e.getMessage(), e);
        return ResponseUtil.error(ErrorCode.RESOURCE_NOT_FOUND.getCode(), ErrorCode.RESOURCE_NOT_FOUND.getMessage());
    }
    
    /**
     * 处理系统异常
     * @param e 系统异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        log.error("System exception occurred: {}", e.getMessage(), e);
        return ResponseUtil.error(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMessage());
    }
}