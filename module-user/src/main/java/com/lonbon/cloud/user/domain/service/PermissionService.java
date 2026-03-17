package com.lonbon.cloud.user.domain.service;

import com.lonbon.cloud.user.domain.dto.PermissionCreateDTO;
import com.lonbon.cloud.user.domain.dto.PermissionUpdateDTO;
import com.lonbon.cloud.user.domain.entity.Permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionService {
    Permission createPermission(PermissionCreateDTO permission);
    Permission updatePermission(UUID id, PermissionUpdateDTO permission);
    void deletePermission(UUID id);
    Optional<Permission> getPermissionById(UUID id);
    List<Permission> getPermissionsByTenantId(UUID tenantId);
    List<Permission> getPermissionsByTeamId(UUID teamId);
    List<Permission> getAllPermissions();
}
