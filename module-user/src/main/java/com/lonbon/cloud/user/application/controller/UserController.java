package com.lonbon.cloud.user.application.controller;

import com.lonbon.cloud.common.utils.Response;
import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public Response<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return Response.success(createdUser, "User created successfully");
        } catch (Exception e) {
            return Response.error("Failed to create user: " + e.getMessage());
        }
    }

    @PutMapping
    public Response<User> updateUser(@RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(user);
            return Response.success(updatedUser, "User updated successfully");
        } catch (Exception e) {
            return Response.error("Failed to update user: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Response<Void> deleteUser(@PathVariable("id") String id) {
        try {
            userService.deleteUser(UUID.fromString(id));
            return Response.success(null, "User deleted successfully");
        } catch (Exception e) {
            return Response.error("Failed to delete user: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Response<User> getUserById(@PathVariable("id") String id) {
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

    @GetMapping("/username/{username}")
    public Response<User> getUserByUsername(@PathVariable("username") String username) {
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

    @GetMapping
    public Response<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return Response.success(users);
        } catch (Exception e) {
            return Response.error("Failed to get users: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/password")
    public Response<Void> updatePassword(@PathVariable("id") String id, @RequestParam("newPassword") String newPassword) {
        try {
            userService.changePassword(UUID.fromString(id), newPassword);
            return Response.success(null, "Password updated successfully");
        } catch (Exception e) {
            return Response.error("Failed to update password: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public Response<Void> updateUserStatus(@PathVariable("id") String id, @RequestParam("active") boolean active) {
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
