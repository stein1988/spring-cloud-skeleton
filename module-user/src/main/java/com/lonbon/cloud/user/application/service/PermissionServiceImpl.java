package com.lonbon.cloud.user.application.service;

import com.lonbon.cloud.user.domain.dto.PermissionCreateDTO;
import com.lonbon.cloud.user.domain.dto.PermissionUpdateDTO;
import com.lonbon.cloud.user.domain.entity.Permission;
import com.lonbon.cloud.user.domain.repository.PermissionRepository;
import com.lonbon.cloud.user.domain.service.PermissionService;
import io.github.linpeilie.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private Converter converter;

    @Override
    public Permission createPermission(PermissionCreateDTO permission) {
        Permission createPermission = converter.convert(permission, Permission.class);
        return permissionRepository.save(createPermission);
    }

    @Override
    public Permission updatePermission(UUID id, PermissionUpdateDTO permission) {
        Optional<Permission> exists = permissionRepository.findById(id);
        if (exists.isPresent()) {
            Permission update = converter.convert(permission, exists.get());
            return permissionRepository.save(update);
        } else throw new RuntimeException("Permission not exists");
    }

    @Override
    public void deletePermission(UUID id) {
        permissionRepository.deleteById(id);
    }

    @Override
    public Optional<Permission> getPermissionById(UUID id) {
        return permissionRepository.findById(id);
    }

    @Override
    public List<Permission> getPermissionsByTenantId(UUID tenantId) {
        // TODO: 实现通过租户ID查询权限的功能
        return (List<Permission>) permissionRepository.findAll();
    }

    @Override
    public List<Permission> getPermissionsByTeamId(UUID teamId) {
        // TODO: 实现通过团队ID查询权限的功能
        return (List<Permission>) permissionRepository.findAll();
    }

    @Override
    public List<Permission> getAllPermissions() {
        return (List<Permission>) permissionRepository.findAll();
    }
}
