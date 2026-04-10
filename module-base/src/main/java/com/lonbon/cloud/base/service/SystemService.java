package com.lonbon.cloud.base.service;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.basic.api.database.CodeFirstCommand;
import com.easy.query.core.basic.api.database.DatabaseCodeFirst;
import com.lonbon.cloud.base.entity.MigrationEntityParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class SystemService {

    protected final EasyEntityQuery easyEntityQuery;

    protected final List<Class<?>> entities;

    public SystemService(EasyEntityQuery easyEntityQuery, List<Class<?>> entities) {
        this.easyEntityQuery = easyEntityQuery;
        this.entities = entities;
    }

    public SystemService(EasyEntityQuery easyEntityQuery, String... packagePaths) {
        this.easyEntityQuery = easyEntityQuery;
        this.entities = scanEntityProxyClasses(packagePaths);
    }

    private List<Class<?>> scanEntityProxyClasses(String... packagePaths) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Table.class));

        List<Class<?>> classes = new ArrayList<>();
        for (String packagePath : packagePaths) {
            Set<BeanDefinition> components = provider.findCandidateComponents(packagePath);
            for (BeanDefinition component : components) {
                try {
                    classes.add(ClassUtils.forName(Objects.requireNonNull(component.getBeanClassName()), null));
                } catch (ClassNotFoundException e) {
                    log.error("Failed to load class: {}", component.getBeanClassName(), e);
                }
            }
        }
        classes.forEach(clazz -> log.info("Scanned entity proxy class: {}", clazz.getName()));
        return classes;
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
