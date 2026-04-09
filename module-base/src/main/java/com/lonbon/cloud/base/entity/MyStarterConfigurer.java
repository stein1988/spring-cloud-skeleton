package com.lonbon.cloud.base.entity;

import com.easy.query.core.bootstrapper.StarterConfigurer;
import com.easy.query.core.inject.ServiceCollection;
import com.easy.query.core.migration.DatabaseMigrationProvider;

public class MyStarterConfigurer implements StarterConfigurer {
    @Override
    public void configure(ServiceCollection services) {
        //addService如果不存在就添加存在就替换
//        services.addService(NameConversion.class, MyNameConversion.class);
        services.addService(DatabaseMigrationProvider.class, MyDatabaseMigrationProvider.class);
    }
}
