package com.lonbon.cloud.base.service;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.basic.api.database.CodeFirstCommand;
import com.easy.query.core.basic.api.database.DatabaseCodeFirst;
import com.lonbon.cloud.base.entity.MigrationEntityParser;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SystemService {

    protected final EasyEntityQuery easyEntityQuery;

    protected final List<Class<?>> entities;

    public SystemService(EasyEntityQuery easyEntityQuery, List<Class<?>> entities) {
        this.easyEntityQuery = easyEntityQuery;
        this.entities = entities;
    }

    public void syncTable() {
        easyEntityQuery.setMigrationParser(new MigrationEntityParser());
        DatabaseCodeFirst databaseCodeFirst = easyEntityQuery.getDatabaseCodeFirst();
        CodeFirstCommand codeFirstCommand = databaseCodeFirst.syncTableCommand(entities);
        codeFirstCommand.executeWithTransaction(arg -> {
            log.info(arg.getSQL());
            arg.commit();
        });
    }
}
