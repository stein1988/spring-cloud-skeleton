package com.lonbon.cloud.user.application.controller;

import com.lonbon.cloud.common.utils.Response;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @PostMapping
    public Response<Tenant> createTenant(@RequestBody Tenant tenant) {
        try {
            Tenant createdTenant = tenantService.createTenant(tenant);
            return Response.success(createdTenant, "Tenant created successfully");
        } catch (Exception e) {
            return Response.error("Failed to create tenant: " + e.getMessage());
        }
    }

    @PutMapping
    public Response<Tenant> updateTenant(@RequestBody Tenant tenant) {
        try {
            Tenant updatedTenant = tenantService.updateTenant(tenant);
            return Response.success(updatedTenant, "Tenant updated successfully");
        } catch (Exception e) {
            return Response.error("Failed to update tenant: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Response<Void> deleteTenant(@PathVariable("id") String id) {
        try {
            tenantService.deleteTenant(UUID.fromString(id));
            return Response.success(null, "Tenant deleted successfully");
        } catch (Exception e) {
            return Response.error("Failed to delete tenant: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Response<Tenant> getTenantById(@PathVariable("id") String id) {
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

    @GetMapping("/name/{name}")
    public Response<Tenant> getTenantByName(@PathVariable("name") String name) {
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

    @GetMapping
    public Response<List<Tenant>> getAllTenants() {
        try {
            List<Tenant> tenants = tenantService.getAllTenants();
            return Response.success(tenants);
        } catch (Exception e) {
            return Response.error("Failed to get tenants: " + e.getMessage());
        }
    }

    @GetMapping("/default")
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

    @PutMapping("/default/{id}")
    public Response<Void> setDefaultTenant(@PathVariable("id") String id) {
        try {
            tenantService.setDefaultTenant(UUID.fromString(id));
            return Response.success(null, "Default tenant set successfully");
        } catch (Exception e) {
            return Response.error("Failed to set default tenant: " + e.getMessage());
        }
    }
}
