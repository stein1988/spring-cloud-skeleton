package com.lonbon.cloud.user.application.service;

import com.lonbon.cloud.base.service.SimpleEntityService;
import com.lonbon.cloud.user.domain.entity.UserTenant;
import com.lonbon.cloud.user.domain.entity.proxy.UserTenantProxy;
import com.lonbon.cloud.user.domain.repository.UserTenantRepository;
import com.lonbon.cloud.user.domain.service.UserTenantService;
import io.github.linpeilie.Converter;
import org.springframework.stereotype.Service;

@Service
public class UserTenantServiceImpl extends SimpleEntityService<UserTenant, UserTenantProxy>
        implements UserTenantService {
    public UserTenantServiceImpl(Converter converter, UserTenantRepository repository) {
        super(converter, repository, UserTenant.class);
    }
}
