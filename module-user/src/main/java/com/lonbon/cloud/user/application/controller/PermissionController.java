package com.lonbon.cloud.user.application.controller;

import com.lonbon.cloud.base.response.Response;
import com.lonbon.cloud.user.domain.dto.PermissionCreateDTO;
import com.lonbon.cloud.user.domain.dto.PermissionUpdateDTO;
import com.lonbon.cloud.user.domain.entity.Permission;
import com.lonbon.cloud.user.domain.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/permissions")
@Tag(name = "权限", description = "权限操作")
public class PermissionController {

    @Resource
    private PermissionService permissionService;

    @PostMapping
    @Operation(summary = "创建", description = "创建权限")
    public Response<UUID> create(@RequestBody @Validated @NotNull PermissionCreateDTO permission) {
        Permission createdPermission = permissionService.createPermission(permission);
        return Response.success(createdPermission.getId(), "Permission created successfully");
    }

    @PostMapping("/{id}/delete")
    @Operation(summary = "删除", description = "删除权限")
    public Response<UUID> delete(@PathVariable("id") UUID id) {
        permissionService.deletePermission(id);
        return Response.success(id, "Permission deleted successfully");
    }

    @PostMapping("/{id}/update")
    @Operation(summary = "更新", description = "更新权限")
    public Response<UUID> update(@PathVariable("id") UUID id, @RequestBody @Validated PermissionUpdateDTO permission) {
        permissionService.updatePermission(id, permission);
        return Response.success(id, "Permission updated successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取", description = "获取权限")
    public Response<Permission> getPermissionById(@PathVariable("id") UUID id) {
        Optional<Permission> permission = permissionService.getPermissionById(id);
        return permission.map(Response::success).orElseGet(() -> Response.error("Permission not found"));
    }

    @GetMapping
    @Operation(summary = "查询所有", description = "查询所有权限")
    public Response<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return Response.success(permissions);
    }
}
