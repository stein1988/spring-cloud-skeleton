package com.lonbon.cloud.base.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 * <p>
 * 用于API接口的响应数据封装，统一返回格式。
 * </p>
 *
 * @param <T> 数据类型
 * @author lonbon
 * @since 1.0.0
 */
@Data
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;

    /**
     * 构造响应
     *
     * @param code    状态码
     * @param message 消息
     * @param data    数据
     */
    private Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应（带数据）
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 响应结果
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(200, "success", data);
    }

    /**
     * 成功响应（带数据和个人消息）
     *
     * @param data    数据
     * @param message 消息
     * @param <T>     数据类型
     * @return 响应结果
     */
    public static <T> Response<T> success(T data, String message) {
        return new Response<>(200, message, data);
    }

    /**
     * 成功响应（无数据）
     *
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> Response<T> success() {
        return new Response<>(200, "success", null);
    }

    /**
     * 错误响应
     *
     * @param code    状态码
     * @param message 消息
     * @param <T>     数据类型
     * @return 响应结果
     */
    public static <T> Response<T> error(int code, String message) {
        return new Response<>(code, message, null);
    }

    /**
     * 错误响应（默认500状态码）
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return 响应结果
     */
    public static <T> Response<T> error(String message) {
        return new Response<>(500, message, null);
    }
}
