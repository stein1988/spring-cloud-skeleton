package com.lonbon.cloud.user.api.controller;

import com.lonbon.cloud.base.response.Response;
import com.lonbon.cloud.user.domain.dto.user.CreateDTO;
import com.lonbon.cloud.user.domain.dto.user.UpdateDTO;
import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.service.UserService;
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
 * 用户控制器
 * <p>
 * 提供用户相关的RESTful API接口，包括用户的创建、删除、更新、查询等操作。
 * </p>
 *
 * @author lonbon
 * @see UserService
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "用户", description = "用户操作")
public class UserController {

    /**
     * 用户服务
     */
    private final UserService userService;

    /**
     * 创建用户
     *
     * @param user 用户创建DTO
     * @return 创建成功的用户ID
     */
    @PostMapping
    @Operation(summary = "创建", description = "创建用户")
    public Response<UUID> create(@RequestBody @Validated @NotNull CreateDTO user) {
        User createdUser = userService.createEntity(user);
        return Response.success(createdUser.getId(), "User created successfully");
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除的用户ID
     */
    @PostMapping("/{id}/delete")
    @Operation(summary = "删除", description = "删除用户")
    public Response<UUID> delete(@PathVariable("id") UUID id) {
        userService.deleteEntity(id);
        return Response.success(id, "User deleted successfully");
    }

    /**
     * 更新用户
     *
     * @param id   用户ID
     * @param user 用户更新DTO
     * @return 更新成功的用户ID
     */
    @PostMapping("/{id}/update")
    @Operation(summary = "更新", description = "更新用户")
    public Response<UUID> update(@PathVariable("id") UUID id, @RequestBody @Validated UpdateDTO user) {
        userService.updateEntity(id, user);
        return Response.success(id, "User updated successfully");
    }

    /**
     * 根据ID获取用户
     *
     * @param id       用户ID
     * @param includes 需要包含的关联数据列表，如 profile, settings
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取", description = "获取用户")
    public Response<User> getUserById(
            @PathVariable("id") UUID id,
            @Parameter(description = "需要包含的关联数据列表，如 profile, settings") @RequestParam(required = false) List<String> includes) {
        Optional<User> user = userService.getEntityById(id, includes, false);
        return user.map(Response::success).orElseGet(() -> Response.error("User not found"));
    }

    /**
     * 查询所有用户
     *
     * @return 所有用户列表
     */
    @GetMapping
    @Operation(summary = "查询所有", description = "查询所有用户")
    public Response<List<User>> getAllUsers() {
        return Response.success(userService.getAllEntities());
    }
}
