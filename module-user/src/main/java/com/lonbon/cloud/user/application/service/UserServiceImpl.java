package com.lonbon.cloud.user.application.service;

import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.repository.UserRepository;
import com.lonbon.cloud.user.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setVersionId(0);
        user.setDeleted(false);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        user.setVersionId(user.getVersionId() + 1);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.delete(id);
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByTenantId(UUID tenantId) {
        return userRepository.findByTenantId(tenantId);
    }

    @Override
    public void changePassword(UUID userId, String newPassword) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 这里应该添加密码加密逻辑
            user.setPasswordHash(newPassword);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    @Override
    public void login(UUID userId, String ipAddress) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setLastLoginAt(LocalDateTime.now());
            user.setLastLoginIp(ipAddress);
            userRepository.save(user);
        }
    }
}
