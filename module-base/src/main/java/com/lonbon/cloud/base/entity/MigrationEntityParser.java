package com.lonbon.cloud.base.entity;

import com.easy.query.core.metadata.ColumnMetadata;
import com.easy.query.core.migration.ColumnDbTypeResult;
import com.easy.query.core.migration.EntityMigrationMetadata;
import com.easy.query.pgsql.migration.PgSQLMigrationEntityParser;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 数据库迁移实体解析器
 * <p>
 * 扩展PostgreSQL迁移解析器，提供统一的类型映射和约束规则。
 * 用于代码优先（Code-First）模式的数据库表结构同步。
 * </p>
 *
 * @author lonbon
 * @since 1.0.0
 */
public class MigrationEntityParser extends PgSQLMigrationEntityParser {

    /**
     * 强制非空的类型集合
     * <p>
     * 为了实现规范化约束，这些类型的字段将默认为NOT NULL。
     * </p>
     */
    private static final Set<Class<?>> NOT_NULL_TYPES = Set.of(Boolean.class, Integer.class, Long.class, String.class);

    /**
     * Java类型到数据库类型的映射
     * <p>
     * 定义了支持的Java类型及其对应的PostgreSQL类型和默认值。
     * </p>
     */
    private static final Map<Class<?>, ColumnDbTypeResult> columnTypeMap = new HashMap<>();

    /**
     * 初始化类型映射
     * <p>
     * 配置各种Java类型对应的数据库类型和默认值：
     * - Boolean -> BOOL, 默认值 FALSE
     * - Integer -> INT4, 默认值 0
     * - Long -> INT8, 默认值 0
     * - String -> TEXT, 默认值 ''
     * - UUID -> UUID, 无默认值
     * - OffsetDateTime -> TIMESTAMPTZ, 无默认值
     * - LocalDate -> DATE, 无默认值
     * </p>
     */
    static {
        columnTypeMap.put(Boolean.class, new ColumnDbTypeResult("BOOL", "FALSE")); // boolean默认为 NOT NULL DEFAULT FALSE
        columnTypeMap.put(Integer.class, new ColumnDbTypeResult("INT4", "0"));
        columnTypeMap.put(Long.class, new ColumnDbTypeResult("INT8", "0"));
        columnTypeMap.put(String.class, new ColumnDbTypeResult("TEXT", "''"));   // String默认为NOT NULL DEFAULT "";
        columnTypeMap.put(UUID.class, new ColumnDbTypeResult("UUID", null));
        columnTypeMap.put(OffsetDateTime.class, new ColumnDbTypeResult("TIMESTAMPTZ", null));
        columnTypeMap.put(LocalDate.class, new ColumnDbTypeResult("DATE", null));
    }

    /**
     * 获取类型映射表
     *
     * @return Java类型到数据库类型的映射
     */
    @Override
    protected Map<Class<?>, ColumnDbTypeResult> getColumnTypeMap() {
        return columnTypeMap;
    }

    /**
     * 获取列的数据库类型
     * <p>
     * 根据字段的Java类型返回对应的数据库类型。
     * 为了实现规范化约束，只支持预定义的类型，其他类型将抛出异常。
     * </p>
     *
     * @param entityMigrationMetadata 实体迁移元数据
     * @param columnMetadata          列元数据
     * @return 列的数据库类型结果
     * @throws IllegalArgumentException 如果类型不支持
     */
    @Override
    public ColumnDbTypeResult getColumnDbType(
            EntityMigrationMetadata entityMigrationMetadata,
            ColumnMetadata columnMetadata) {

        /* 为实现规范化约束，只支持map中定义的类型，其他类型抛出异常，如需要增加其他类型，需要在map中添加对应的类型和默认值，或者修改此处代码。 */
        Class<?> propertyType = columnMetadata.getPropertyType();
        // 检查是否支持该类型
        if (!columnTypeMap.containsKey(propertyType)) {
            String className = entityMigrationMetadata.getEntityMetadata().getEntityClass().getName();
            String fieldName = columnMetadata.getName();
            Set<Class<?>> allowTypes = columnTypeMap.keySet();
            throw new IllegalArgumentException(
                    "Unsupported type: " + propertyType.getName() + " in class " + className + ", field: " + fieldName + ". Only " + allowTypes + " are allowed.");
        }
        return super.getColumnDbType(entityMigrationMetadata, columnMetadata);
    }

    /**
     * 判断列是否可为空
     * <p>
     * 为了实现规范化约束，Boolean、Integer、Long、String类型默认为NOT NULL，
     * 其他基本类型在父类中已判定为NOT NULL。
     * </p>
     *
     * @param entityMigrationMetadata 实体迁移元数据
     * @param columnMetadata          列元数据
     * @return 是否可为空
     */
    @Override
    public boolean isNullable(EntityMigrationMetadata entityMigrationMetadata, ColumnMetadata columnMetadata) {

        Class<?> propertyType = columnMetadata.getPropertyType();
        /* 为实现规范化约束，Boolean、Integer、Long、String类型默认为NOT NULL，其他基本类型在super中已经判定为NOT NULL */
        if (NOT_NULL_TYPES.contains(propertyType)) {
            return false;
        }

        return super.isNullable(entityMigrationMetadata, columnMetadata);
    }


}
