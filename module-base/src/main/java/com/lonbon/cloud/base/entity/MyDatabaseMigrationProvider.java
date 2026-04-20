package com.lonbon.cloud.base.entity;

import com.easy.query.core.configuration.dialect.SQLKeyword;
import com.easy.query.core.migration.MigrationEntityParser;
import com.easy.query.pgsql.migration.PgSQLDatabaseMigrationProvider;

import javax.sql.DataSource;

/**
 * 数据库迁移提供者
 * <p>
 * 继承自 PostgreSQL 数据库迁移提供者，用于管理数据库表的创建、修改和迁移。
 * 支持自动检测数据库表结构与实体类之间的差异，并生成相应的迁移脚本。
 * </p>
 *
 * @author lonbon
 * @since 1.0.0
 */
public class MyDatabaseMigrationProvider extends PgSQLDatabaseMigrationProvider {
    
    /**
     * 构造数据库迁移提供者
     *
     * @param dataSource             数据源
     * @param sqlKeyword             SQL关键字处理器
     * @param migrationEntityParser  迁移实体解析器
     */
    public MyDatabaseMigrationProvider(
            DataSource dataSource, SQLKeyword sqlKeyword,
            MigrationEntityParser migrationEntityParser) {
        super(dataSource, sqlKeyword, migrationEntityParser);
    }

    /**
     * 同步表结构
     * <p>
     * 比较数据库表结构与实体类定义的差异，生成相应的DDL语句。
     * 支持添加新列、重命名列等操作。
     * </p>
     *
     * @param tableMigrationData 表迁移数据
     * @param oldTable           是否为旧表
     * @return 迁移命令列表
     */
//    @Override
//    public List<MigrationCommand> syncTable(TableMigrationData tableMigrationData, boolean oldTable) {
//
//        //比较差异
//        Set<String> tableColumns = getColumnNames(tableMigrationData, oldTable);
//
//        ArrayList<MigrationCommand> migrationCommands = new ArrayList<>();
//        for (ColumnMigrationData column : tableMigrationData.getColumns()) {
//            if (!tableColumns.contains(column.getName())) {
//                String oldColumnName = column.getOldColumnName();
//                if (EasyStringUtil.isNotBlank(oldColumnName) && tableColumns.contains(oldColumnName)) {
//                    MigrationCommand migrationCommand = renameColumn(tableMigrationData, oldColumnName, column);
//                    migrationCommands.add(migrationCommand);
//                } else {
//                    MigrationCommand migrationCommand = addColumn(tableMigrationData, column);
//                    migrationCommands.add(migrationCommand);
//                }
//            }
//        }
//        return migrationCommands;
//    }

}
