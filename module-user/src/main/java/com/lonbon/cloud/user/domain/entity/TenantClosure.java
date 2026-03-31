package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.TenantClosureProxy;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_tenant_closure", ignoreProperties = {"tenantId", "updateTime", "updateBy", "version"})
@EntityProxy
public class TenantClosure extends BaseEntity implements ProxyEntityAvailable<TenantClosure, TenantClosureProxy> {

    /**
     * 祖先租户ID
     */
    private UUID ancestorId;

    /**
     * 后代租户ID
     */
    private UUID descendantId;

    /**
     * 层级距离，取值>=0，0表示自身，ancestorId=descendantId，1表示直接后代
     */
//    @Min(value = 0, message = "层级距离（distance）不能为负数")
    private int distance;
}
