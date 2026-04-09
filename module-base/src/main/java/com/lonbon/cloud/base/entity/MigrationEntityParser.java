package com.lonbon.cloud.base.entity;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.metadata.ColumnMetadata;
import com.easy.query.core.migration.ColumnDbTypeResult;
import com.easy.query.core.migration.EntityMigrationMetadata;
import com.easy.query.pgsql.migration.PgSQLMigrationEntityParser;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MigrationEntityParser extends PgSQLMigrationEntityParser {

    private static final Map<Class<?>, ColumnDbTypeResult> columnTypeMap = new HashMap<>();

    static {
        columnTypeMap.put(boolean.class, new ColumnDbTypeResult("BOOL", "FALSE")); // boolean默认为 NOT NULL DEFAULT FALSE
        columnTypeMap.put(Boolean.class, new ColumnDbTypeResult("BOOL", null));
        columnTypeMap.put(float.class, new ColumnDbTypeResult("FLOAT4", "0"));
        columnTypeMap.put(Float.class, new ColumnDbTypeResult("FLOAT4", null));
        columnTypeMap.put(double.class, new ColumnDbTypeResult("FLOAT8", "0"));
        columnTypeMap.put(Double.class, new ColumnDbTypeResult("FLOAT8", null));
        columnTypeMap.put(short.class, new ColumnDbTypeResult("INT2", "0"));
        columnTypeMap.put(Short.class, new ColumnDbTypeResult("INT2", null));
        columnTypeMap.put(int.class, new ColumnDbTypeResult("INT4", "0"));
        columnTypeMap.put(Integer.class, new ColumnDbTypeResult("INT4", null));
        columnTypeMap.put(long.class, new ColumnDbTypeResult("INT8", "0"));
        columnTypeMap.put(Long.class, new ColumnDbTypeResult("INT8", null));
        columnTypeMap.put(byte.class, new ColumnDbTypeResult("INT2", "0"));
        columnTypeMap.put(Byte.class, new ColumnDbTypeResult("INT2", null));
        columnTypeMap.put(BigDecimal.class, new ColumnDbTypeResult("numeric(16,2)", null));
        columnTypeMap.put(LocalDateTime.class, new ColumnDbTypeResult("TIMESTAMP", null));
        columnTypeMap.put(LocalDate.class, new ColumnDbTypeResult("DATE", null));
        columnTypeMap.put(LocalTime.class, new ColumnDbTypeResult("TIME", null));
        columnTypeMap.put(OffsetDateTime.class, new ColumnDbTypeResult("TIMESTAMPTZ", null));
        columnTypeMap.put(String.class, new ColumnDbTypeResult("TEXT", "''"));   // String默认为NOT NULL DEFAULT "";
        columnTypeMap.put(UUID.class, new ColumnDbTypeResult("UUID", null));
    }

    @Override
    protected Map<Class<?>, ColumnDbTypeResult> getColumnTypeMap() {
        return columnTypeMap;
    }


//    @Override
//    @NotNull
//    public ColumnDbTypeResult getColumnDbType(
//            EntityMigrationMetadata entityMigrationMetadata,
//            ColumnMetadata columnMetadata) {
//
//        Class<?> propertyType = columnMetadata.getPropertyType();
//        if (OffsetDateTime.class.equals(propertyType)) {
//            Column annotation = getColumnAnnotation(entityMigrationMetadata, columnMetadata);
//            String defaultValue = annotation != null && !annotation.dbDefault()
//                                                                   .isBlank() ? annotation.dbDefault() : "NOW()";
//            return new ColumnDbTypeResult("TIMESTAMPTZ", defaultValue);
//        }
//
//
//        return super.getColumnDbType(entityMigrationMetadata, columnMetadata);
//    }

    @Override
    public boolean isNullable(EntityMigrationMetadata entityMigrationMetadata, ColumnMetadata columnMetadata) {

        Class<?> propertyType = columnMetadata.getPropertyType();
        if (String.class.equals(propertyType)) {
            return false;
        }

        return super.isNullable(entityMigrationMetadata, columnMetadata);
    }

    @Nullable
    private Column getColumnAnnotation(EntityMigrationMetadata entityMigrationMetadata, ColumnMetadata columnMetadata) {
        Field declaredField = entityMigrationMetadata.getFieldByColumnMetadata(columnMetadata);
        return declaredField.getAnnotation(Column.class);
    }


}
