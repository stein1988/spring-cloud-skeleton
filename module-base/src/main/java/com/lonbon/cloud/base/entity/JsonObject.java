package com.lonbon.cloud.base.entity;

/**
 * JSON对象接口
 * <p>
 * 用于标识需要序列化为JSON格式的字段类型。
 * 实现了此接口的类将使用 {@link JsonObjectTypeHandler} 进行数据库读写时的类型转换。
 * </p>
 *
 * @author lonbon
 * @since 1.0.0
 * @see JsonObjectTypeHandler
 * @see JsonObjectAutoConverter
 */
public interface JsonObject {
}
