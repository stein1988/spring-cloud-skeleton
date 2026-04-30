package com.lonbon.cloud.user.api.controller;

import com.lonbon.cloud.base.response.Response;
import com.lonbon.cloud.user.domain.dto.RoleCreateDTO;
import com.lonbon.cloud.user.domain.dto.RoleUpdateDTO;
import com.lonbon.cloud.user.domain.entity.Role;
import com.lonbon.cloud.user.domain.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "角色", description = "角色操作")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "创建", description = "创建角色")
    public Response<UUID> create(@RequestBody @Validated @NotNull RoleCreateDTO role) {
        Role createdRole = roleService.createEntity(role);
        return Response.success(createdRole.getId(), "Role created successfully");
    }

    @PostMapping("/{id}/delete")
    @Operation(summary = "删除", description = "删除角色")
    public Response<UUID> delete(@PathVariable("id") UUID id) {
        roleService.deleteEntity(id);
        return Response.success(id, "Role deleted successfully");
    }

    @PostMapping("/{id}/update")
    @Operation(summary = "更新", description = "更新角色")
    public Response<UUID> update(@PathVariable("id") UUID id, @RequestBody @Validated RoleUpdateDTO role) {
        roleService.updateEntity(id, role);
        return Response.success(id, "Role updated successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取", description = "获取角色")
    public Response<Role> getRoleById(@PathVariable("id") UUID id) {
        Optional<Role> role = roleService.getEntityById(id);
        return role.map(Response::success).orElseGet(() -> Response.error("Role not found"));
    }

    @GetMapping
    @Operation(summary = "查询所有", description = "查询所有角色")
    public Response<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllEntities();
        return Response.success(roles);
    }
}
