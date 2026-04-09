package com.lonbon.cloud.base.entity;

import com.easy.query.core.configuration.dialect.SQLKeyword;
import com.easy.query.core.migration.MigrationEntityParser;
import com.easy.query.pgsql.migration.PgSQLDatabaseMigrationProvider;

import javax.sql.DataSource;

public class MyDatabaseMigrationProvider extends PgSQLDatabaseMigrationProvider {
    public MyDatabaseMigrationProvider(
            DataSource dataSource, SQLKeyword sqlKeyword,
            MigrationEntityParser migrationEntityParser) {
        super(dataSource, sqlKeyword, migrationEntityParser);
    }

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
