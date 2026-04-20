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

@Slf4j
@RestController
@RequestMapping("/api/departments")
@Tag(name = "部门", description = "部门操作")
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @PostMapping
    @Operation(summary = "创建", description = "创建部门")
    public Response<UUID> create(@RequestBody @Validated @NotNull DepartmentCreateDTO department) {
        Department createdDepartment = departmentService.createEntity(department);
        return Response.success(createdDepartment.getId(), "Department created successfully");
    }

    @PostMapping("/{id}/delete")
    @Operation(summary = "删除", description = "删除部门")
    public Response<UUID> delete(@PathVariable("id") UUID id) {
        departmentService.deleteEntity(id);
        return Response.success(id, "Department deleted successfully");
    }

    @PostMapping("/{id}/update")
    @Operation(summary = "更新", description = "更新部门")
    public Response<UUID> update(@PathVariable("id") UUID id, @RequestBody @Validated DepartmentUpdateDTO department) {
        departmentService.updateEntity(id, department);
        return Response.success(id, "Department updated successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取", description = "获取部门")
    public Response<Department> getDepartmentById(
            @PathVariable("id") UUID id,
            @Parameter(description = "需要包含的关联数据列表") @RequestParam(required = false) List<String> includes) {
        Optional<Department> department = departmentService.getEntityById(id, includes, false);
        return department.map(Response::success).orElseGet(() -> Response.error("Department not found"));
    }

    @GetMapping
    @Operation(summary = "查询所有", description = "查询所有部门")
    public Response<List<Department>> getAllDepartments() {
        return Response.success(departmentService.getAllEntities());
    }

    @GetMapping("/{id}/children")
    @Operation(summary = "获取直接子部门", description = "获取指定部门的直接子部门")
    public Response<List<Department>> getDirectChildren(@PathVariable("id") UUID id) {
        return Response.success(departmentService.getDirectChildren(id));
    }

    @GetMapping("/{id}/descendants")
    @Operation(summary = "获取所有后代部门", description = "获取指定部门的所有后代部门（包括多级）")
    public Response<List<Department>> getDescendants(@PathVariable("id") UUID id) {
        return Response.success(departmentService.getDescendants(id));
    }

    @GetMapping("/{id}/parent")
    @Operation(summary = "获取直接父部门", description = "获取指定部门的直接父部门")
    public Response<Department> getDirectParent(@PathVariable("id") UUID id) {
        Optional<Department> parent = departmentService.getDirectParent(id);
        return parent.map(Response::success).orElseGet(() -> Response.error("Parent department not found"));
    }

    @GetMapping("/{id}/ancestors")
    @Operation(summary = "获取所有祖先部门", description = "获取指定部门的所有祖先部门")
    public Response<List<Department>> getAllAncestors(@PathVariable("id") UUID id) {
        return Response.success(departmentService.getAllAncestors(id));
    }

    @PostMapping("/{id}/move")
    @Operation(summary = "移动部门", description = "将部门移动到新的父部门下")
    public Response<UUID> moveNode(
            @PathVariable("id") UUID id,
            @RequestParam("newParentId") UUID newParentId) {
        Department moved = departmentService.moveNode(id, newParentId);
        return Response.success(moved.getId(), "Department moved successfully");
    }

    @GetMapping("/{id}/tree")
    @Operation(summary = "获取部门树", description = "以指定部门为根节点获取部门树")
    public Response<Department> getTree(@PathVariable("id") UUID id) {
        Department tree = departmentService.getTree(id);
        return Response.success(tree, "Department tree retrieved successfully");
    }

}
