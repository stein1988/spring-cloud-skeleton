package com.lonbon.cloud.base.entity;

import com.easy.query.core.bootstrapper.StarterConfigurer;
import com.easy.query.core.inject.ServiceCollection;
import com.easy.query.core.migration.DatabaseMigrationProvider;

/**
 * EasyQuery启动配置器
 * <p>
 * 用于配置EasyQuery框架的全局服务，包括自定义的数据库迁移提供者等。
 * 通过实现 {@link StarterConfigurer} 接口，可以在框架初始化时注册自定义服务。
 * </p>
 *
 * @author lonbon
 * @since 1.0.0
 */
public class MyStarterConfigurer implements StarterConfigurer {
    
    /**
     * 配置EasyQuery服务
     * <p>
     * 注册自定义的数据库迁移提供者，替换默认实现。
     * </p>
     *
     * @param services 服务集合
     */
    @Override
    public void configure(ServiceCollection services) {
        //addService如果不存在就添加存在就替换
//        services.addService(NameConversion.class, MyNameConversion.class);
        services.addService(DatabaseMigrationProvider.class, MyDatabaseMigrationProvider.class);
    }
}
