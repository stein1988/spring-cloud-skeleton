package com.lonbon.cloud.user.application.controller;

import com.lonbon.cloud.common.utils.Response;
import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.service.UserService;
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
@Mapping("/api/users")
public class UserController {

    @Inject
    private UserService userService;

    @Post
    @Mapping
    public Response<User> createUser(User user) {
        try {
            User createdUser = userService.createUser(user);
            return Response.success(createdUser, "User created successfully");
        } catch (Exception e) {
            return Response.error("Failed to create user: " + e.getMessage());
        }
    }

    @Put
    @Mapping
    public Response<User> updateUser(User user) {
        try {
            User updatedUser = userService.updateUser(user);
            return Response.success(updatedUser, "User updated successfully");
        } catch (Exception e) {
            return Response.error("Failed to update user: " + e.getMessage());
        }
    }

    @Delete
    @Mapping("/{id}")
    public Response<Void> deleteUser(@Path("id") String id) {
        try {
            userService.deleteUser(UUID.fromString(id));
            return Response.success(null, "User deleted successfully");
        } catch (Exception e) {
            return Response.error("Failed to delete user: " + e.getMessage());
        }
    }

    @Get
    @Mapping("/{id}")
    public Response<User> getUserById(@Path("id") String id) {
        try {
            Optional<User> user = userService.getUserById(UUID.fromString(id));
            if (user.isPresent()) {
                return Response.success(user.get());
            } else {
                return Response.error("User not found");
            }
        } catch (Exception e) {
            return Response.error("Failed to get user: " + e.getMessage());
        }
    }

    @Get
    @Mapping("/username/{username}")
    public Response<User> getUserByUsername(@Path("username") String username) {
        try {
            Optional<User> user = userService.getUserByUsername(username);
            if (user.isPresent()) {
                return Response.success(user.get());
            } else {
                return Response.error("User not found");
            }
        } catch (Exception e) {
            return Response.error("Failed to get user: " + e.getMessage());
        }
    }

    @Get
    @Mapping
    public Response<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return Response.success(users);
        } catch (Exception e) {
            return Response.error("Failed to get users: " + e.getMessage());
        }
    }

    @Put
    @Mapping("/{id}/password")
    public Response<Void> updatePassword(@Path("id") String id, String newPassword) {
        try {
            userService.changePassword(UUID.fromString(id), newPassword);
            return Response.success(null, "Password updated successfully");
        } catch (Exception e) {
            return Response.error("Failed to update password: " + e.getMessage());
        }
    }

    @Put
    @Mapping("/{id}/status")
    public Response<Void> updateUserStatus(@Path("id") String id, boolean active) {
        try {
            // 暂时通过updateUser方法来更新用户状态
            Optional<User> userOptional = userService.getUserById(UUID.fromString(id));
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setActive(active);
                userService.updateUser(user);
                return Response.success(null, "User status updated successfully");
            } else {
                return Response.error("User not found");
            }
        } catch (Exception e) {
            return Response.error("Failed to update user status: " + e.getMessage());
        }
    }
}
