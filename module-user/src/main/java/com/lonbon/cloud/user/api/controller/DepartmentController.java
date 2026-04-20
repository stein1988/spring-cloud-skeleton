package com.lonbon.cloud.user.api.controller;

import com.lonbon.cloud.base.response.Response;
import com.lonbon.cloud.user.domain.dto.DepartmentCreateDTO;
import com.lonbon.cloud.user.domain.dto.DepartmentUpdateDTO;
import com.lonbon.cloud.user.domain.entity.Department;
import com.lonbon.cloud.user.domain.service.DepartmentService;
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

/**
 * 部门控制器
 * <p>
 * 提供部门相关的RESTful API接口，包括部门的创建、删除、更新、查询以及树形结构操作。
 * 支持部门层级关系查询（父子关系、祖先后代关系）。
 * </p>
 *
 * @author lonbon
 * @since 1.0.0
 * @see DepartmentService
 */
@Slf4j
@RestController
@RequestMapping("/api/departments")
@Tag(name = "部门", description = "部门操作")
public class DepartmentController {

    /**
     * 部门服务
     */
    @Resource
    private DepartmentService departmentService;

    /**
     * 创建部门
     *
     * @param department 部门创建DTO
     * @return 创建成功的部门ID
     */
    @PostMapping
    @Operation(summary = "创建", description = "创建部门")
    public Response<UUID> create(@RequestBody @Validated @NotNull DepartmentCreateDTO department) {
        Department createdDepartment = departmentService.createEntity(department);
        return Response.success(createdDepartment.getId(), "Department created successfully");
    }

    /**
     * 删除部门
     *
     * @param id 部门ID
     * @return 删除的部门ID
     */
    @PostMapping("/{id}/delete")
    @Operation(summary = "删除", description = "删除部门")
    public Response<UUID> delete(@PathVariable("id") UUID id) {
        departmentService.deleteEntity(id);
        return Response.success(id, "Department deleted successfully");
    }

    /**
     * 更新部门
     *
     * @param id        部门ID
     * @param department 部门更新DTO
     * @return 更新成功的部门ID
     */
    @PostMapping("/{id}/update")
    @Operation(summary = "更新", description = "更新部门")
    public Response<UUID> update(@PathVariable("id") UUID id, @RequestBody @Validated DepartmentUpdateDTO department) {
        departmentService.updateEntity(id, department);
        return Response.success(id, "Department updated successfully");
    }

    /**
     * 根据ID获取部门
     *
     * @param id       部门ID
     * @param includes 需要包含的关联数据列表
     * @return 部门信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取", description = "获取部门")
    public Response<Department> getDepartmentById(
            @PathVariable("id") UUID id,
            @Parameter(description = "需要包含的关联数据列表") @RequestParam(required = false) List<String> includes) {
        Optional<Department> department = departmentService.getEntityById(id, includes, false);
        return department.map(Response::success).orElseGet(() -> Response.error("Department not found"));
    }

    /**
     * 查询所有部门
     *
     * @return 所有部门列表
     */
    @GetMapping
    @Operation(summary = "查询所有", description = "查询所有部门")
    public Response<List<Department>> getAllDepartments() {
        return Response.success(departmentService.getAllEntities());
    }

    /**
     * 获取直接子部门
     *
     * @param id 部门ID
     * @return 直接子部门列表
     */
    @GetMapping("/{id}/children")
    @Operation(summary = "获取直接子部门", description = "获取指定部门的直接子部门")
    public Response<List<Department>> getDirectChildren(@PathVariable("id") UUID id) {
        return Response.success(departmentService.getDirectChildren(id));
    }

    /**
     * 获取所有后代部门
     *
     * @param id 部门ID
     * @return 所有后代部门列表（包括多级）
     */
    @GetMapping("/{id}/descendants")
    @Operation(summary = "获取所有后代部门", description = "获取指定部门的所有后代部门（包括多级）")
    public Response<List<Department>> getDescendants(@PathVariable("id") UUID id) {
        return Response.success(departmentService.getDescendants(id));
    }

    /**
     * 获取直接父部门
     *
     * @param id 部门ID
     * @return 直接父部门
     */
    @GetMapping("/{id}/parent")
    @Operation(summary = "获取直接父部门", description = "获取指定部门的直接父部门")
    public Response<Department> getDirectParent(@PathVariable("id") UUID id) {
        Optional<Department> parent = departmentService.getDirectParent(id);
        return parent.map(Response::success).orElseGet(() -> Response.error("Parent department not found"));
    }

    /**
     * 获取所有祖先部门
     *
     * @param id 部门ID
     * @return 所有祖先部门列表
     */
    @GetMapping("/{id}/ancestors")
    @Operation(summary = "获取所有祖先部门", description = "获取指定部门的所有祖先部门")
    public Response<List<Department>> getAllAncestors(@PathVariable("id") UUID id) {
        return Response.success(departmentService.getAllAncestors(id));
    }

    /**
     * 移动部门
     * <p>
     * 将部门移动到新的父部门下，会更新闭包表中的层级关系。
     * </p>
     *
     * @param id          要移动的部门ID
     * @param newParentId 新的父部门ID
     * @return 移动后的部门ID
     */
    @PostMapping("/{id}/move")
    @Operation(summary = "移动部门", description = "将部门移动到新的父部门下")
    public Response<UUID> moveNode(
            @PathVariable("id") UUID id,
            @RequestParam("newParentId") UUID newParentId) {
        Department moved = departmentService.moveNode(id, newParentId);
        return Response.success(moved.getId(), "Department moved successfully");
    }

    /**
     * 获取部门树
     *
     * @param id 根节点部门ID
     * @return 部门树（包含所有子节点）
     */
    @GetMapping("/{id}/tree")
    @Operation(summary = "获取部门树", description = "以指定部门为根节点获取部门树")
    public Response<Department> getTree(@PathVariable("id") UUID id) {
        Department tree = departmentService.getTree(id);
        return Response.success(tree, "Department tree retrieved successfully");
    }

}
