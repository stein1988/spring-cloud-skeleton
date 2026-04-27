package com.lonbon.cloud.base.service;

public interface EntityServiceInterceptor<T> {
    default void preCreate(T entity) {
    }

    default void postCreate(T entity) {
    }
}
