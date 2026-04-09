package com.lonbon.cloud.user.application.service;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.base.service.SystemService;
import com.lonbon.cloud.user.domain.entity.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class SystemServiceImpl extends SystemService {
    public SystemServiceImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery,
              Arrays.asList(Tenant.class, TenantClosure.class, Department.class, DepartmentClosure.class, User.class));
    }
}
