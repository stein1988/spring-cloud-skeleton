package com.lonbon.cloud.user.application.service;

import com.lonbon.cloud.base.service.SimpleEntityService;
import com.lonbon.cloud.user.domain.entity.Role;
import com.lonbon.cloud.user.domain.entity.proxy.RoleProxy;
import com.lonbon.cloud.user.domain.repository.RoleRepository;
import com.lonbon.cloud.user.domain.service.RoleService;
import io.github.linpeilie.Converter;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends SimpleEntityService<RoleProxy, Role, RoleRepository> implements RoleService {
    public RoleServiceImpl(Converter converter, RoleRepository repository) {
        super(converter, Role.class, repository);
    }
}
