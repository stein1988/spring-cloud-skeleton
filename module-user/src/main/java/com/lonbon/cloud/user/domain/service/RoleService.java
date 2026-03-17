package com.lonbon.cloud.user.domain.service;

import com.lonbon.cloud.user.domain.dto.RoleCreateDTO;
import com.lonbon.cloud.user.domain.dto.RoleUpdateDTO;
import com.lonbon.cloud.user.domain.entity.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleService {
    Role createRole(RoleCreateDTO role);
    Role updateRole(UUID id, RoleUpdateDTO role);
    void deleteRole(UUID id);
    Optional<Role> getRoleById(UUID id);
    List<Role> getRolesByTenantId(UUID tenantId);
    List<Role> getRolesByTeamId(UUID teamId);
    List<Role> getAllRoles();
}
