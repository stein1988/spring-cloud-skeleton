package com.lonbon.cloud.user.application.service;

import com.lonbon.cloud.user.domain.dto.RoleCreateDTO;
import com.lonbon.cloud.user.domain.dto.RoleUpdateDTO;
import com.lonbon.cloud.user.domain.entity.Role;
import com.lonbon.cloud.user.domain.repository.RoleRepository;
import com.lonbon.cloud.user.domain.service.RoleService;
import io.github.linpeilie.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private Converter converter;

    @Override
    public Role createRole(RoleCreateDTO role) {
        Role createRole = converter.convert(role, Role.class);
        return roleRepository.save(createRole);
    }

    @Override
    public Role updateRole(UUID id, RoleUpdateDTO role) {
        Optional<Role> exists = roleRepository.findById(id);
        if (exists.isPresent()) {
            Role update = converter.convert(role, exists.get());
            return roleRepository.save(update);
        } else throw new RuntimeException("Role not exists");
    }

    @Override
    public void deleteRole(UUID id) {
        roleRepository.deleteById(id);
    }

    @Override
    public Optional<Role> getRoleById(UUID id) {
        return roleRepository.findById(id);
    }

    @Override
    public List<Role> getRolesByTenantId(UUID tenantId) {
        // TODO: 实现通过租户ID查询角色的功能
        return (List<Role>) roleRepository.findAll();
    }

    @Override
    public List<Role> getRolesByTeamId(UUID teamId) {
        // TODO: 实现通过团队ID查询角色的功能
        return (List<Role>) roleRepository.findAll();
    }

    @Override
    public List<Role> getAllRoles() {
        return (List<Role>) roleRepository.findAll();
    }
}
