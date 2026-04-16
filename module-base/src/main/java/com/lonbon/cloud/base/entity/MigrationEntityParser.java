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

public class MigrationEntityParser extends PgSQLMigrationEntityParser {

    private static final Set<Class<?>> NOT_NULL_TYPES = Set.of(Boolean.class, Integer.class, Long.class, String.class);

    private static final Map<Class<?>, ColumnDbTypeResult> columnTypeMap = new HashMap<>();

    static {
        columnTypeMap.put(Boolean.class, new ColumnDbTypeResult("BOOL", "FALSE")); // boolean默认为 NOT NULL DEFAULT FALSE
        columnTypeMap.put(Integer.class, new ColumnDbTypeResult("INT4", "0"));
        columnTypeMap.put(Long.class, new ColumnDbTypeResult("INT8", "0"));
        columnTypeMap.put(String.class, new ColumnDbTypeResult("TEXT", "''"));   // String默认为NOT NULL DEFAULT "";
        columnTypeMap.put(UUID.class, new ColumnDbTypeResult("UUID", null));
        columnTypeMap.put(OffsetDateTime.class, new ColumnDbTypeResult("TIMESTAMPTZ", null));
        columnTypeMap.put(LocalDate.class, new ColumnDbTypeResult("DATE", null));
    }

    @Override
    protected Map<Class<?>, ColumnDbTypeResult> getColumnTypeMap() {
        return columnTypeMap;
    }

    @Override
    public ColumnDbTypeResult getColumnDbType(
            EntityMigrationMetadata entityMigrationMetadata,
            ColumnMetadata columnMetadata) {

        /* 为实现规范化约束，只支持map中定义的类型，其他类型抛出异常，如需要增加其他类型，需要在map中添加对应的类型和默认值，或者修改此处代码。 */
        Class<?> propertyType = columnMetadata.getPropertyType();
        if (!columnTypeMap.containsKey(propertyType)) {
            String className = entityMigrationMetadata.getEntityMetadata().getEntityClass().getName();
            String fieldName = columnMetadata.getName();
            Set<Class<?>> allowTypes = columnTypeMap.keySet();
            throw new IllegalArgumentException(
                    "Unsupported type: " + propertyType.getName() + " in class " + className + ", field: " + fieldName + ". Only " + allowTypes + " are allowed.");
        }
        return super.getColumnDbType(entityMigrationMetadata, columnMetadata);
    }


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
