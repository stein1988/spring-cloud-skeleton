package com.lonbon.cloud.user.application.controller;

import com.lonbon.cloud.common.utils.Response;
import com.lonbon.cloud.user.domain.dto.TenantCreateDTO;
import com.lonbon.cloud.user.domain.dto.TenantUpdateDTO;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
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
    public Response<Tenant> getTenantById(@PathVariable("id") UUID id) {
        Optional<Tenant> tenant = tenantService.getTenantById(id);
        return tenant.map(Response::success).orElseGet(() -> Response.error("Tenant not found"));
    }

//    @Operation(summary = "查询单个（根据名称）")
//    @Get
//    @Mapping("/name/{name}")
//    public Response<Tenant> getTenantByName(@Path("name") String name) {
//        try {
//            Optional<Tenant> tenant = tenantService.getTenantByName(name);
//            return tenant.map(Response::success).orElseGet(() -> Response.error("Tenant not found"));
//        } catch (Exception e) {
//            return Response.error("Failed to get tenant: " + e.getMessage());
//        }
//    }
//
//    @Operation(summary = "查询所有")
//    @Get
//    @Mapping
//    public Response<List<Tenant>> getAllTenants() {
//        try {
//           Tenant newTenant = new Tenant();
//            newTenant.setName("测试租户");
//            tenantService.createTenant(newTenant);
//            List<Tenant> tenants = tenantService.getAllTenants();
//            for (Tenant tenant : tenants) {
//                log.info(tenant.toString());
//            }
//            return Response.success(tenants);
//        } catch (Exception e) {
//            return Response.error("Failed to get tenants: " + e.getMessage());
//        }
//    }
//
//    @Get
//    @Mapping("/default")
//    public Response<Tenant> getDefaultTenant() {
//        try {
//            Optional<Tenant> tenant = tenantService.getDefaultTenant();
//            return tenant.map(Response::success).orElseGet(() -> Response.error("Default tenant not found"));
//        } catch (Exception e) {
//            return Response.error("Failed to get default tenant: " + e.getMessage());
//        }
//    }
//
//    @Put
//    @Mapping("/default/{id}")
//    public Response<Void> setDefaultTenant(@Path("id") String id) {
//        try {
//            tenantService.setDefaultTenant(UUID.fromString(id));
//            return Response.success(null, "Default tenant set successfully");
//        } catch (Exception e) {
//            return Response.error("Failed to set default tenant: " + e.getMessage());
//        }
//    }
}
