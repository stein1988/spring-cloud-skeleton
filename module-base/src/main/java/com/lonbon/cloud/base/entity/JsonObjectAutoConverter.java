package com.lonbon.cloud.base.entity;

import com.easy.query.core.basic.extension.conversion.ValueAutoConverter;
import com.easy.query.core.metadata.ColumnMetadata;
import com.easy.query.core.util.EasyClassUtil;
import com.easy.query.core.util.EasyMapUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于Jackson实现的JsonObject自动转换器
 * 替换原snack4的ONode实现，功能完全兼容
 */
@Component
public class JsonObjectAutoConverter implements ValueAutoConverter<Object, Object> {

    // Jackson核心序列化工具（单例，线程安全）
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    // 类型缓存，避免重复反射获取字段类型
    private static final Map<ColumnMetadata, Type> cacheMap = new ConcurrentHashMap<>();

    /**
     * 判断当前字段类型是否需要应用该转换器（保留原有逻辑）
     */
    @Override
    public boolean apply(Class<?> entityClass, Class<Object> propertyType, String property) {
        return JsonObject.class.isAssignableFrom(propertyType);
    }

    /**
     * 序列化：将JsonObject对象转为JSON字符串（替换ONode.serialize）
     */
    @Override
    public @Nullable Object serialize(@Nullable Object o, ColumnMetadata columnMetadata) {
        if (o == null) {
            return null;
        }
        try {
            // Jackson序列化对象为JSON字符串
            return OBJECT_MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException("JSON序列化失败：" + columnMetadata.getPropertyName(), e);
        }
    }

    /**
     * 反序列化：将JSON字符串转为指定类型的JsonObject对象（替换ONode.deserialize）
     */
    @Override
    public @Nullable Object deserialize(@Nullable Object s, ColumnMetadata columnMetadata) {
//        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonStr = getValueString(s);
        if (jsonStr == null || jsonStr.isEmpty()) {
            return null;
        }
        try {
            Type targetType = getFiledType(columnMetadata);
            if (targetType instanceof Class<?>)
                // 处理泛型类型反序列化（Jackson核心）
                return OBJECT_MAPPER.readValue(jsonStr, (Class<?>) targetType);
            else throw new RuntimeException("not support type:" + targetType);
        } catch (Exception e) {
            throw new RuntimeException("JSON反序列化失败：" + columnMetadata.getPropertyName(), e);
        }
    }

    /**
     * 保留原有逻辑：将入参转为字符串（兼容PGobject等类型）
     */
    private @Nullable String getValueString(@Nullable Object s) {
        return switch (s) {
            case null -> null;
            case String string -> string;
            default -> s.toString();     // 兼容PGobject jsonb类型的toString()
        };
    }

    /**
     * 保留原有逻辑：从缓存获取字段类型，避免重复反射
     */
    private Type getFiledType(ColumnMetadata columnMetadata) {
        return EasyMapUtil.computeIfAbsent(cacheMap, columnMetadata, this::getFiledType0);
    }

    /**
     * 保留原有逻辑：反射获取字段的泛型类型
     */
    private Type getFiledType0(ColumnMetadata columnMetadata) {
        Class<?> entityClass = columnMetadata.getEntityMetadata().getEntityClass();
        Field declaredField = EasyClassUtil.getFieldByName(entityClass, columnMetadata.getPropertyName());
        return declaredField.getGenericType();
    }
}