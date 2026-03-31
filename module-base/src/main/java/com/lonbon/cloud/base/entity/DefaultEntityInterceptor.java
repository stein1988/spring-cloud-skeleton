package com.lonbon.cloud.base.entity;

import com.easy.query.core.basic.extension.interceptor.EntityInterceptor;
import com.easy.query.core.basic.extension.interceptor.UpdateEntityColumnInterceptor;
import com.easy.query.core.basic.extension.interceptor.UpdateSetInterceptor;
import com.easy.query.core.expression.parser.core.base.ColumnOnlySelector;
import com.easy.query.core.expression.parser.core.base.ColumnSetter;
import com.easy.query.core.expression.sql.builder.EntityInsertExpressionBuilder;
import com.easy.query.core.expression.sql.builder.EntityUpdateExpressionBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 默认实体拦截器
 * 用于处理BaseEntity的自动填充逻辑，如创建时间、更新时间、创建人、更新人等
 */
@Component
public class DefaultEntityInterceptor implements EntityInterceptor, UpdateSetInterceptor, UpdateEntityColumnInterceptor {

    @Override
    public String name() {
        return "DEFAULT_INTERCEPTOR"; // 后续禁用拦截器或者启用拦截器使用这个名称代表当前拦截器
    }

    /**
     * 哪些对象需要用到这个拦截器(继承BaseEntity的对象)
     */
    @Override
    public boolean apply(@NotNull Class<?> entityClass) {
        return BaseEntity.class.isAssignableFrom(entityClass);
    }

    /**
     * insert操作时的处理
     */
    @Override
    public void configureInsert(Class<?> entityClass, EntityInsertExpressionBuilder entityInsertExpressionBuilder, Object entity) {
        BaseEntity baseEntity = (BaseEntity) entity;

        OffsetDateTime now = OffsetDateTime.now();

        if (baseEntity.getCreateTime() == null) {
            baseEntity.setCreateTime(now);
        }
        if (baseEntity.getCreateBy() == null) {
            // TODO：获取当前用户id
            baseEntity.setCreateBy(UUID.randomUUID());
        }
        if (baseEntity.getUpdateTime() == null) {
            baseEntity.setUpdateTime(now);
        }
        if (baseEntity.getUpdateBy() == null) {
            // TODO：获取当前用户id
            baseEntity.setUpdateBy(UUID.randomUUID());
        }
    }

    /**
     * update操作时的处理（不包括伪删除）
     */
    @Override
    public void configureUpdate(Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, Object entity) {
        BaseEntity baseEntity = (BaseEntity) entity;

        baseEntity.setUpdateTime(OffsetDateTime.now());

        // TODO：获取当前用户id
        baseEntity.setUpdateBy(UUID.randomUUID());
    }

    /**
     * 表达式更新时的处理
     * 目前未实现具体逻辑
     */
    @Override
    public void configure(@NotNull Class<?> entityClass, @NotNull EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, @NotNull ColumnOnlySelector<Object> columnSelector, @NotNull Object entity) {

    }

    /**
     * 列更新时的处理
     * 目前未实现具体逻辑
     */
    @Override
    public void configure(Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, ColumnSetter<Object> columnSetter) {

    }
}