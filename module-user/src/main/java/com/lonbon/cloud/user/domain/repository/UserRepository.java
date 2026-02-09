package com.lonbon.cloud.user.domain.repository;

import com.lonbon.cloud.user.domain.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    void delete(UUID id);
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    List<User> findByTenantId(UUID tenantId);
}
