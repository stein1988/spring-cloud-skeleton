package com.lonbon.cloud.user.api.controller;

import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;
import com.lonbon.cloud.base.response.Response;
import com.lonbon.cloud.user.domain.dto.tenant.CreateDTO;
import com.lonbon.cloud.user.domain.dto.tenant.QueryDTO;
import com.lonbon.cloud.user.domain.dto.tenant.UpdateDTO;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 租户控制器
 * <p>
 * 提供租户相关的RESTful API接口，包括租户的创建、删除、更新、查询以及树形结构操作。
 * 支持分页查询和层级关系查询（父子关系、祖先后代关系）。
 * </p>
 *
 * @author lonbon
 * @see TenantService
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
@Tag(name = "租户", description = "租户操作")
public class TenantController {

    /**
     * 租户服务
     */
    private final TenantService tenantService;

    /**
     * 创建租户
     *
     * @param tenant 租户创建DTO
     * @return 创建成功的租户ID
     */
    @PostMapping
    @Operation(summary = "创建", description = "创建租户")
    public Response<UUID> create(@RequestBody @Validated @NotNull CreateDTO tenant) {
        Tenant createdTenant = tenantService.createEntity(tenant);
        return Response.success(createdTenant.getId(), "Tenant created successfully");
    }

    /**
     * 删除租户
     *
     * @param id 租户ID
     * @return 删除的租户ID
     */
    @PostMapping("/{id}/delete")
    @Operation(summary = "删除", description = "删除租户")
    public Response<UUID> delete(@PathVariable("id") UUID id) {
        tenantService.deleteEntity(id);
        return Response.success(id, "Tenant deleted successfully");
    }

    /**
     * 更新租户
     *
     * @param id     租户ID
     * @param tenant 租户更新DTO
     * @return 更新成功的租户ID
     */
    @PostMapping("/{id}/update")
    @Operation(summary = "更新", description = "更新租户")
    public Response<UUID> update(@PathVariable("id") UUID id, @RequestBody @Validated UpdateDTO tenant) {
        tenantService.updateEntity(id, tenant);
        return Response.success(id, "Tenant updated successfully");
    }

    /**
     * 根据ID获取租户
     *
     * @param id 租户ID
     * @return 租户信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取", description = "获取租户")
    public Response<Tenant> getTenantById(
            @PathVariable("id") @Parameter(description = "租户唯一标识，采用UUID格式，32位16进制字符串", required = true, example =
                    "123e4567-e89b-12d3-a456-426614174000") UUID id) {
        Optional<Tenant> tenant = tenantService.getEntityById(id);
        return tenant.map(Response::success).orElseGet(() -> Response.error("Tenant not found"));
    }

    /**
     * 分页查询租户
     *
     * @param query    查询条件
     * @param pageable 分页参数
     * @return 分页后的租户列表
     */
    @GetMapping()
    @Operation(summary = "分页查询", description = "分页查询租户")
    public Response<PageResult<Tenant>> getTenants(QueryDTO query, Pageable pageable) {
        log.info("query: {}, pageable: {}", query, pageable);
        PageResult<Tenant> tenants = tenantService.getPaginationEntities(query, pageable);
        return Response.success(tenants);
    }

    /**
     * 获取直接子租户
     *
     * @param id 租户ID
     * @return 直接子租户列表
     */
    @GetMapping("/{id}/children")
    @Operation(summary = "获取直接子租户", description = "获取指定租户的直接子租户")
    public Response<List<Tenant>> getDirectChildren(@PathVariable("id") UUID id) {
        return Response.success(tenantService.getDirectChildren(id));
    }

    /**
     * 获取所有后代租户
     *
     * @param id 租户ID
     * @return 所有后代租户列表（包括多级）
     */
    @GetMapping("/{id}/descendants")
    @Operation(summary = "获取所有后代租户", description = "获取指定租户的所有后代租户（包括多级）")
    public Response<List<Tenant>> getDescendants(@PathVariable("id") UUID id) {
        return Response.success(tenantService.getDescendants(id));
    }

    /**
     * 获取直接父租户
     *
     * @param id 租户ID
     * @return 直接父租户
     */
    @GetMapping("/{id}/parent")
    @Operation(summary = "获取直接父租户", description = "获取指定租户的直接父租户")
    public Response<Tenant> getDirectParent(@PathVariable("id") UUID id) {
        Optional<Tenant> parent = tenantService.getDirectParent(id);
        return parent.map(Response::success).orElseGet(() -> Response.error("Parent tenant not found"));
    }

    /**
     * 获取所有祖先租户
     *
     * @param id 租户ID
     * @return 所有祖先租户列表
     */
    @GetMapping("/{id}/ancestors")
    @Operation(summary = "获取所有祖先租户", description = "获取指定租户的所有祖先租户")
    public Response<List<Tenant>> getAllAncestors(@PathVariable("id") UUID id) {
        return Response.success(tenantService.getAllAncestors(id));
    }

    /**
     * 移动租户
     * <p>
     * 将租户移动到新的父租户下，会更新闭包表中的层级关系。
     * </p>
     *
     * @param id          要移动的租户ID
     * @param newParentId 新的父租户ID
     * @return 移动后的租户ID
     */
    @PostMapping("/{id}/move")
    @Operation(summary = "移动租户", description = "将租户移动到新的父租户下")
    public Response<UUID> moveNode(@PathVariable("id") UUID id, @RequestParam("newParentId") UUID newParentId) {
        Tenant moved = tenantService.moveNode(id, newParentId);
        return Response.success(moved.getId(), "Tenant moved successfully");
    }

    /**
     * 获取租户树
     *
     * @param id 根节点租户ID
     * @return 租户树（包含所有子节点）
     */
    @GetMapping("/{id}/tree")
    @Operation(summary = "获取租户树", description = "以指定租户为根节点获取租户树")
    public Response<Tenant> getTree(@PathVariable("id") UUID id) {
        Tenant tree = tenantService.getTree(id);
        return Response.success(tree, "Tenant tree retrieved successfully");
    }

}
