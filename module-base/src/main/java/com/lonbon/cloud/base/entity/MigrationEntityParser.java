package com.lonbon.cloud.base.entity;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.metadata.ColumnMetadata;
import com.easy.query.core.migration.ColumnDbTypeResult;
import com.easy.query.core.migration.EntityMigrationMetadata;
import com.easy.query.pgsql.migration.PgSQLMigrationEntityParser;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MigrationEntityParser extends PgSQLMigrationEntityParser {

    private static final Map<Class<?>, ColumnDbTypeResult> columnTypeMap = new HashMap<>();

    static {
        columnTypeMap.put(boolean.class, new ColumnDbTypeResult("BOOL", "FALSE")); // boolean默认为 NOT NULL DEFAULT FALSE
        columnTypeMap.put(int.class, new ColumnDbTypeResult("INT4", "0"));
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
    @NotNull
    public ColumnDbTypeResult getColumnDbType(
            EntityMigrationMetadata entityMigrationMetadata,
            ColumnMetadata columnMetadata) {

        Class<?> propertyType = columnMetadata.getPropertyType();
        ColumnDbTypeResult result = columnTypeMap.get(propertyType);
        if (result == null) {
            throw new IllegalArgumentException("Unsupported type: " + propertyType.getName() + ". Only boolean, int, String, UUID, OffsetDateTime, LocalDate are allowed.");
        }
        return result;
    }

    @Override
    public boolean isNullable(EntityMigrationMetadata entityMigrationMetadata, ColumnMetadata columnMetadata) {

        Class<?> propertyType = columnMetadata.getPropertyType();
        if (String.class.equals(propertyType)) {
            return false;
        }

        return super.isNullable(entityMigrationMetadata, columnMetadata);
    }


}
