package com.lonbon.cloud.common.utils;

import lombok.Data;

import java.io.Serializable;

@Data
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;

    private Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(200, "success", data);
    }

    public static <T> Response<T> success(T data, String message) {
        return new Response<>(200, message, data);
    }

    public static <T> Response<T> success() {
        return new Response<>(200, "success", null);
    }

    public static <T> Response<T> error(int code, String message) {
        return new Response<>(code, message, null);
    }

    public static <T> Response<T> error(String message) {
        return new Response<>(500, message, null);
    }
}
