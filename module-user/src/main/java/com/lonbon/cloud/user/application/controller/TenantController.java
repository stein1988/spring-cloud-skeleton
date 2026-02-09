package com.lonbon.cloud.user.application.controller;

import com.lonbon.cloud.common.utils.Response;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.service.TenantService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Delete;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.annotation.Put;
import org.noear.solon.annotation.Path;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@Mapping("/api/tenants")
public class TenantController {

    @Inject
    private TenantService tenantService;

    @Post
    @Mapping
    public Response<Tenant> createTenant(Tenant tenant) {
        try {
            Tenant createdTenant = tenantService.createTenant(tenant);
            return Response.success(createdTenant, "Tenant created successfully");
        } catch (Exception e) {
            return Response.error("Failed to create tenant: " + e.getMessage());
        }
    }

    @Put
    @Mapping
    public Response<Tenant> updateTenant(Tenant tenant) {
        try {
            Tenant updatedTenant = tenantService.updateTenant(tenant);
            return Response.success(updatedTenant, "Tenant updated successfully");
        } catch (Exception e) {
            return Response.error("Failed to update tenant: " + e.getMessage());
        }
    }

    @Delete
    @Mapping("/{id}")
    public Response<Void> deleteTenant(@Path("id") String id) {
        try {
            tenantService.deleteTenant(UUID.fromString(id));
            return Response.success(null, "Tenant deleted successfully");
        } catch (Exception e) {
            return Response.error("Failed to delete tenant: " + e.getMessage());
        }
    }

    @Get
    @Mapping("/{id}")
    public Response<Tenant> getTenantById(@Path("id") String id) {
        try {
            Optional<Tenant> tenant = tenantService.getTenantById(UUID.fromString(id));
            if (tenant.isPresent()) {
                return Response.success(tenant.get());
            } else {
                return Response.error("Tenant not found");
            }
        } catch (Exception e) {
            return Response.error("Failed to get tenant: " + e.getMessage());
        }
    }

    @Get
    @Mapping("/name/{name}")
    public Response<Tenant> getTenantByName(@Path("name") String name) {
        try {
            Optional<Tenant> tenant = tenantService.getTenantByName(name);
            if (tenant.isPresent()) {
                return Response.success(tenant.get());
            } else {
                return Response.error("Tenant not found");
            }
        } catch (Exception e) {
            return Response.error("Failed to get tenant: " + e.getMessage());
        }
    }

    @Get
    @Mapping
    public Response<List<Tenant>> getAllTenants() {
        try {
            List<Tenant> tenants = tenantService.getAllTenants();
            return Response.success(tenants);
        } catch (Exception e) {
            return Response.error("Failed to get tenants: " + e.getMessage());
        }
    }

    @Get
    @Mapping("/default")
    public Response<Tenant> getDefaultTenant() {
        try {
            Optional<Tenant> tenant = tenantService.getDefaultTenant();
            if (tenant.isPresent()) {
                return Response.success(tenant.get());
            } else {
                return Response.error("Default tenant not found");
            }
        } catch (Exception e) {
            return Response.error("Failed to get default tenant: " + e.getMessage());
        }
    }

    @Put
    @Mapping("/default/{id}")
    public Response<Void> setDefaultTenant(@Path("id") String id) {
        try {
            tenantService.setDefaultTenant(UUID.fromString(id));
            return Response.success(null, "Default tenant set successfully");
        } catch (Exception e) {
            return Response.error("Failed to set default tenant: " + e.getMessage());
        }
    }
}
