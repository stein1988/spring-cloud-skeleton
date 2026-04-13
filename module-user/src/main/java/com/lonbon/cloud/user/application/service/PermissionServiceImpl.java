package com.lonbon.cloud.user.application.service;

import com.lonbon.cloud.base.service.SimpleEntityService;
import com.lonbon.cloud.user.domain.entity.Permission;
import com.lonbon.cloud.user.domain.entity.proxy.PermissionProxy;
import com.lonbon.cloud.user.domain.repository.PermissionRepository;
import com.lonbon.cloud.user.domain.service.PermissionService;
import io.github.linpeilie.Converter;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl extends SimpleEntityService<Permission, PermissionProxy>
        implements PermissionService {
    public PermissionServiceImpl(Converter converter, PermissionRepository repository) {
        super(converter, repository, Permission.class);
    }
}
