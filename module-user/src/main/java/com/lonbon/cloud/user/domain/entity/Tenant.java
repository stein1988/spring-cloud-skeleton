package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.*;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.TenantProxy;
import com.lonbon.cloud.user.domain.filter.TenantClosureFilter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_tenant", ignoreProperties = {"tenantId"})
@EntityProxy
public class Tenant extends BaseEntity implements ProxyEntityAvailable<Tenant , TenantProxy> {

    /**
     * 类型
     */
    private String type;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 域名
     */
    private String domain;

    /**
     * 祖先列表
     */
    @Navigate(value = RelationTypeEnum.OneToMany,
            selfProperty = {"id"},
            targetProperty = {"descendantId"},
            orderByProps = @OrderByProperty(property = "distance"),
            extraFilter = TenantClosureFilter.class)
    private List<TenantClosure> ancestors;

    /**
     * 后代列表
     */
    @Navigate(value = RelationTypeEnum.OneToMany,
            selfProperty = {"id"},
            targetProperty = {"ancestorId"},
            orderByProps = @OrderByProperty(property = "distance"),
            extraFilter = TenantClosureFilter.class)
    private List<TenantClosure> descendants;

    /**
     * 创建一个自身关系的闭包，用于创建租户时插入
     */
    public TenantClosure createSelfClosure() {
        return new TenantClosure(id, id, 0);
    }


}
