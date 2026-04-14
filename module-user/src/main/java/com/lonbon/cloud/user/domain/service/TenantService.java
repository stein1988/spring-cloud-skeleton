package com.lonbon.cloud.user.domain.service;

import com.lonbon.cloud.base.service.ClosureService;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.entity.TenantClosure;
import com.lonbon.cloud.user.domain.entity.proxy.TenantClosureProxy;
import com.lonbon.cloud.user.domain.entity.proxy.TenantProxy;

public interface TenantService extends ClosureService<Tenant, TenantProxy, TenantClosure, TenantClosureProxy> {

}
