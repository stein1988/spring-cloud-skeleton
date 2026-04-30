package com.lonbon.cloud.base.entity;

import com.easy.query.core.annotation.Table;
import com.easy.query.core.basic.extension.interceptor.EntityInterceptor;
import com.easy.query.core.basic.extension.interceptor.PredicateFilterInterceptor;
import com.easy.query.core.basic.extension.interceptor.UpdateEntityColumnInterceptor;
import com.easy.query.core.basic.extension.interceptor.UpdateSetInterceptor;
import com.easy.query.core.expression.parser.core.base.ColumnOnlySelector;
import com.easy.query.core.expression.parser.core.base.ColumnSetter;
import com.easy.query.core.expression.parser.core.base.WherePredicate;
import com.easy.query.core.expression.sql.builder.EntityInsertExpressionBuilder;
import com.easy.query.core.expression.sql.builder.EntityUpdateExpressionBuilder;
import com.easy.query.core.expression.sql.builder.LambdaEntityExpressionBuilder;
import com.lonbon.cloud.base.satoken.SaTokenHelper;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认实体拦截器
 * 用于处理BaseEntity的自动填充逻辑，如创建时间、更新时间、创建人、更新人等
 */
@Component
public class DefaultEntityInterceptor
        implements EntityInterceptor, PredicateFilterInterceptor, UpdateSetInterceptor, UpdateEntityColumnInterceptor {

    private final Map<Class<?>, Set<String>> ignorePropertiesCache = new ConcurrentHashMap<>();

    @Override
    public String name() {
        return "DEFAULT_INTERCEPTOR"; // 后续禁用拦截器或者启用拦截器使用这个名称代表当前拦截器
    }

    /**
     * 哪些对象需要用到这个拦截器(继承BaseEntity的对象)
     */
    @Override
    public boolean apply(Class<?> entityClass) {
        return BaseEntity.class.isAssignableFrom(entityClass);
    }

    /**
     * insert操作时的处理
     */
    @Override
    public void configureInsert(
            Class<?> entityClass, EntityInsertExpressionBuilder entityInsertExpressionBuilder, Object entity) {
        BaseEntity baseEntity = (BaseEntity) entity;

        OffsetDateTime now = OffsetDateTime.now();
        UUID currentUserId = null;

        if (baseEntity.getCreatedAt() == null) {
            baseEntity.setCreatedAt(now);
        }
        if (baseEntity.getCreatedBy() == null) {
            currentUserId = getCurrentUserId(currentUserId);
            baseEntity.setCreatedBy(currentUserId);
        }
        if (baseEntity.getUpdatedAt() == null) {
            baseEntity.setUpdatedAt(now);
        }
        if (baseEntity.getUpdatedBy() == null) {
            currentUserId = getCurrentUserId(currentUserId);
            baseEntity.setUpdatedBy(currentUserId);
        }
        if (baseEntity.getTenantId() == null) {
            baseEntity.setTenantId(getCurrentTenantId());
        }
    }

    /**
     * update操作时的处理（不包括伪删除）
     */
    @Override
    public void configureUpdate(
            Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, Object entity) {
        BaseEntity baseEntity = (BaseEntity) entity;

        baseEntity.setUpdatedAt(OffsetDateTime.now());
        baseEntity.setUpdatedBy(getCurrentUserId(null));
    }

    /**
     * 表达式更新时的处理，实现UpdateEntityColumnInterceptor
     * 目前未实现具体逻辑
     */
    @Override
    public void configure(
            Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder,
            ColumnOnlySelector<Object> columnSelector, Object entity) {

    }

    /**
     * 列更新时的处理，实现UpdateSetInterceptor
     * 目前未实现具体逻辑
     */
    @Override
    public void configure(
            Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder,
            ColumnSetter<Object> columnSetter) {

    }

    /**
     * 构建where条件时的处理，实现PredicateFilterInterceptor
     * 实现基于租户的过滤
     */
    @Override
    public void configure(
            Class<?> entityClass, LambdaEntityExpressionBuilder lambdaEntityExpressionBuilder,
            WherePredicate<Object> wherePredicate) {

        Set<String> ignoreProperties = getIgnoreProperties(entityClass);
        if (!ignoreProperties.contains(BaseEntity.Fields.tenantId)) {
            UUID currentTenantId = getCurrentTenantId();
            if (currentTenantId != SaTokenHelper.NULL_UUID) {
                wherePredicate.eq(BaseEntity.Fields.tenantId, currentTenantId);
            }
        }
    }


    private UUID getCurrentUserId(@Nullable UUID currentUserId) {
        if (currentUserId != null) {
            return currentUserId;
        }

        return SaTokenHelper.getLoginId();
    }

    private UUID getCurrentTenantId() {
        return SaTokenHelper.getCurrentTenantId();
    }

    private Set<String> getIgnoreProperties(Class<?> entityClass) {
        return ignorePropertiesCache.computeIfAbsent(entityClass, clazz -> {
            Table table = clazz.getAnnotation(Table.class);
            if (table != null && table.ignoreProperties().length > 0) {
                return Set.of(table.ignoreProperties());
            }
            return Set.of();
        });
    }

}