package com.lonbon.cloud.user.application.controller;

import com.easy.query.core.api.pagination.EasyPageResult;
import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;
import com.lonbon.cloud.common.utils.Response;
import com.lonbon.cloud.user.domain.dto.TenantCreateDTO;
import com.lonbon.cloud.user.domain.dto.TenantQueryDTO;
import com.lonbon.cloud.user.domain.dto.TenantUpdateDTO;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/tenants")
@Tag(name = "租户", description = "租户操作")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @PostMapping
    @Operation(summary = "创建", description = "创建description")
    public Response<UUID> create(@RequestBody @Validated @NotNull TenantCreateDTO tenant) {
        Tenant createdTenant = tenantService.createTenant(tenant);
        return Response.success(createdTenant.getId(), "Tenant created successfully");
    }

    @PostMapping("/{id}/delete")
    @Operation(summary = "删除", description = "删除description")
    public Response<UUID> delete(@PathVariable("id") UUID id) {
        tenantService.deleteTenant(id);
        return Response.success(id, "Tenant deleted successfully");
    }

    @PostMapping("/{id}/update")
    @Operation(summary = "更新", description = "更新description")
    public Response<UUID> update(@PathVariable("id") UUID id, @RequestBody @Validated TenantUpdateDTO tenant) {
        tenantService.updateTenant(id, tenant);
        return Response.success(id, "Tenant updated successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取", description = "获取description")
    public Response<Tenant> getTenantById(
            @PathVariable("id")
            @Parameter(description = "用户唯一标识，采用UUID格式，32位16进制字符串",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000")
            UUID id) {
        Optional<Tenant> tenant = tenantService.getTenantById(id);
        return tenant.map(Response::success).orElseGet(() -> Response.error("Tenant not found"));
    }

    @GetMapping()
    @Operation(summary = "查询", description = "查询description")
    public Response<PageResult<Tenant>> getTenants(
            TenantQueryDTO query,
            Pageable pageable
    ) {
        log.info("query: {}, pageable: {}", query, pageable);
        PageResult<Tenant> tenants = tenantService.getTenants(query, pageable);
        return Response.success(tenants);
    }
}
