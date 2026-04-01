package com.lonbon.cloud.user.application.controller;

import com.lonbon.cloud.base.response.Response;
import com.lonbon.cloud.user.domain.dto.UserCreateDTO;
import com.lonbon.cloud.user.domain.dto.UserUpdateDTO;
import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.service.UserService;
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
@RequestMapping("/api/users")
@Tag(name = "用户", description = "用户操作")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping
    @Operation(summary = "创建", description = "创建用户")
    public Response<UUID> create(@RequestBody @Validated @NotNull UserCreateDTO user) {
        User createdUser = userService.createEntity(user);
        return Response.success(createdUser.getId(), "User created successfully");
    }

    @PostMapping("/{id}/delete")
    @Operation(summary = "删除", description = "删除用户")
    public Response<UUID> delete(@PathVariable("id") UUID id) {
        userService.deleteEntity(id);
        return Response.success(id, "User deleted successfully");
    }

    @PostMapping("/{id}/update")
    @Operation(summary = "更新", description = "更新用户")
    public Response<UUID> update(@PathVariable("id") UUID id, @RequestBody @Validated UserUpdateDTO user) {
        userService.updateEntity(id, user);
        return Response.success(id, "User updated successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取", description = "获取用户")
    public Response<User> getUserById(@PathVariable("id") UUID id) {
        Optional<User> user = userService.getEntityById(id);
        return user.map(Response::success).orElseGet(() -> Response.error("User not found"));
    }

    @GetMapping
    @Operation(summary = "查询所有", description = "查询所有用户")
    public Response<List<User>> getAllUsers() {
        return Response.success(userService.getAllEntities());
    }
}
