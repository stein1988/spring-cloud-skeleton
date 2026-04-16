package com.lonbon.cloud.user.application.service;

import com.lonbon.cloud.base.service.SimpleEntityService;
import com.lonbon.cloud.user.domain.entity.Role;
import com.lonbon.cloud.user.domain.entity.proxy.RoleProxy;
import com.lonbon.cloud.user.domain.repository.RoleRepository;
import com.lonbon.cloud.user.domain.service.RoleService;
import io.github.linpeilie.Converter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RoleServiceImpl extends SimpleEntityService<Role, RoleProxy> implements RoleService {
    public RoleServiceImpl(Converter converter, RoleRepository repository) {
        super(converter, repository, Role.class);
    }

    @Override
    public List<Role> getRolesByUserId(UUID userId) {
        // TODO：实现
        return new ArrayList<>();
    }
}
