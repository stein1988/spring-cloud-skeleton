package com.lonbon.cloud.user.api.controller;

import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;
import com.lonbon.cloud.base.response.Response;
import com.lonbon.cloud.user.domain.dto.TenantCreateDTO;
import com.lonbon.cloud.user.domain.dto.TenantQueryDTO;
import com.lonbon.cloud.user.domain.dto.TenantUpdateDTO;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/tenants")
@Tag(name = "租户", description = "租户操作")
public class TenantController {

    @Resource
    private TenantService tenantService;

    @PostMapping
    @Operation(summary = "创建", description = "创建description")
    public Response<UUID> create(@RequestBody @Validated @NotNull TenantCreateDTO tenant) {
        Tenant createdTenant = tenantService.createEntity(tenant);
        return Response.success(createdTenant.getId(), "Tenant created successfully");
    }

    @PostMapping("/{id}/delete")
    @Operation(summary = "删除", description = "删除description")
    public Response<UUID> delete(@PathVariable("id") UUID id) {
        tenantService.deleteEntity(id);
        return Response.success(id, "Tenant deleted successfully");
    }

    @PostMapping("/{id}/update")
    @Operation(summary = "更新", description = "更新description")
    public Response<UUID> update(@PathVariable("id") UUID id, @RequestBody @Validated TenantUpdateDTO tenant) {
        tenantService.updateEntity(id, tenant);
        return Response.success(id, "Tenant updated successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取", description = "获取description")
    public Response<Tenant> getTenantById(
            @PathVariable("id") @Parameter(description = "用户唯一标识，采用UUID格式，32位16进制字符串", required = true, example =
                    "123e4567-e89b-12d3-a456-426614174000") UUID id) {
        Optional<Tenant> tenant = tenantService.getEntityById(id);
        return tenant.map(Response::success).orElseGet(() -> Response.error("Tenant not found"));
    }

    @GetMapping()
    @Operation(summary = "查询", description = "查询description")
    public Response<PageResult<Tenant>> getTenants(TenantQueryDTO query, Pageable pageable) {
        log.info("query: {}, pageable: {}", query, pageable);
        PageResult<Tenant> tenants = tenantService.getPaginationEntities(query, pageable);
        return Response.success(tenants);
    }

    @GetMapping("/{id}/children")
    @Operation(summary = "获取直接子租户", description = "获取指定租户的直接子租户")
    public Response<List<Tenant>> getDirectChildren(@PathVariable("id") UUID id) {
        return Response.success(tenantService.getDirectChildren(id));
    }

    @GetMapping("/{id}/descendants")
    @Operation(summary = "获取所有后代租户", description = "获取指定租户的所有后代租户（包括多级）")
    public Response<List<Tenant>> getDescendants(@PathVariable("id") UUID id) {
        return Response.success(tenantService.getDescendants(id));
    }

    @GetMapping("/{id}/parent")
    @Operation(summary = "获取直接父租户", description = "获取指定租户的直接父租户")
    public Response<Tenant> getDirectParent(@PathVariable("id") UUID id) {
        Optional<Tenant> parent = tenantService.getDirectParent(id);
        return parent.map(Response::success).orElseGet(() -> Response.error("Parent tenant not found"));
    }

    @GetMapping("/{id}/ancestors")
    @Operation(summary = "获取所有祖先租户", description = "获取指定租户的所有祖先租户")
    public Response<List<Tenant>> getAllAncestors(@PathVariable("id") UUID id) {
        return Response.success(tenantService.getAllAncestors(id));
    }

    @PostMapping("/{id}/move")
    @Operation(summary = "移动租户", description = "将租户移动到新的父租户下")
    public Response<UUID> moveNode(
            @PathVariable("id") UUID id,
            @RequestParam("newParentId") UUID newParentId) {
        Tenant moved = tenantService.moveNode(id, newParentId);
        return Response.success(moved.getId(), "Tenant moved successfully");
    }

    @GetMapping("/{id}/tree")
    @Operation(summary = "获取租户树", description = "以指定租户为根节点获取租户树")
    public Response<Tenant> getTree(@PathVariable("id") UUID id) {
        Tenant tree = tenantService.getTree(id);
        return Response.success(tree, "Tenant tree retrieved successfully");
    }

}
