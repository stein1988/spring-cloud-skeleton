package com.lonbon.cloud.user.domain.service;

import com.lonbon.cloud.base.service.ClosureService;
import com.lonbon.cloud.base.service.EntityService;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.entity.TenantClosure;
import com.lonbon.cloud.user.domain.entity.proxy.TenantClosureProxy;
import com.lonbon.cloud.user.domain.entity.proxy.TenantProxy;

/**
 * 租户服务接口
 * <p>
 * 继承自闭包服务接口，提供租户相关的业务操作和树形结构操作。
 * 支持查询父子关系、祖先后代关系以及租户移动等功能。
 * </p>
 *
 * @author lonbon
 * @since 1.0.0
 * @see ClosureService
 */
public interface TenantService extends EntityService<Tenant, TenantProxy>, ClosureService<Tenant, TenantProxy, TenantClosure, TenantClosureProxy> {

}
