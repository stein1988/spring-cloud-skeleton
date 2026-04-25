package com.lonbon.cloud.user.application.service;

import com.lonbon.cloud.base.service.EntityServiceImpl;
import com.lonbon.cloud.user.domain.entity.Role;
import com.lonbon.cloud.user.domain.entity.proxy.RoleProxy;
import com.lonbon.cloud.user.domain.repository.RoleRepository;
import com.lonbon.cloud.user.domain.service.RoleService;
import io.github.linpeilie.Converter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleServiceImpl extends EntityServiceImpl<Role, RoleProxy> implements RoleService {
    public RoleServiceImpl(Converter converter, RoleRepository repository) {
        super(converter, repository, Role.class);
    }

    @Override
    public List<Role> getRolesByUserId(UUID userId) {
        return repository.getAll(r -> r.userRoles().any(userRole -> userRole.userId().eq(userId)));
    }
}
