package com.lonbon.cloud.base.entity;

import com.easy.query.core.basic.extension.interceptor.EntityInterceptor;
import com.easy.query.core.basic.extension.interceptor.UpdateEntityColumnInterceptor;
import com.easy.query.core.basic.extension.interceptor.UpdateSetInterceptor;
import com.easy.query.core.expression.parser.core.base.ColumnOnlySelector;
import com.easy.query.core.expression.parser.core.base.ColumnSetter;
import com.easy.query.core.expression.sql.builder.EntityInsertExpressionBuilder;
import com.easy.query.core.expression.sql.builder.EntityUpdateExpressionBuilder;
import com.lonbon.cloud.base.satoken.LoginUser;
import com.lonbon.cloud.base.satoken.SaTokenHelper;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 默认实体拦截器
 * 用于处理BaseEntity的自动填充逻辑，如创建时间、更新时间、创建人、更新人等
 */
@Component
public class DefaultEntityInterceptor
        implements EntityInterceptor, UpdateSetInterceptor, UpdateEntityColumnInterceptor {

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

        if (baseEntity.getCreateTime() == null) {
            baseEntity.setCreateTime(now);
        }
        if (baseEntity.getCreateBy() == null) {
            currentUserId = getCurrentUserId(currentUserId);
            baseEntity.setCreateBy(currentUserId);
        }
        if (baseEntity.getUpdateTime() == null) {
            baseEntity.setUpdateTime(now);
        }
        if (baseEntity.getUpdateBy() == null) {
            currentUserId = getCurrentUserId(currentUserId);
            baseEntity.setUpdateBy(currentUserId);
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

        baseEntity.setUpdateTime(OffsetDateTime.now());
        baseEntity.setUpdateBy(getCurrentUserId(null));
    }

    /**
     * 表达式更新时的处理
     * 目前未实现具体逻辑
     */
    @Override
    public void configure(
            Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder,
            ColumnOnlySelector<Object> columnSelector, Object entity) {

    }

    /**
     * 列更新时的处理
     * 目前未实现具体逻辑
     */
    @Override
    public void configure(
            Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder,
            ColumnSetter<Object> columnSetter) {

    }


    private UUID getCurrentUserId(@Nullable UUID currentUserId) {
        if (currentUserId != null) {
            return currentUserId;
        }

        return SaTokenHelper.getLoginId();
    }

    private UUID getCurrentTenantId() {
        LoginUser loginUser = SaTokenHelper.getLoginUser();
        return loginUser.getCurrentTenantId();
    }
}