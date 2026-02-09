package com.lonbon.cloud.user.domain.service;

import com.lonbon.cloud.user.domain.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(UUID id);
    Optional<User> getUserById(UUID id);
    Optional<User> getUserByUsername(String username);
    List<User> getAllUsers();
    List<User> getUsersByTenantId(UUID tenantId);
    void changePassword(UUID userId, String newPassword);
    void login(UUID userId, String ipAddress);
}
