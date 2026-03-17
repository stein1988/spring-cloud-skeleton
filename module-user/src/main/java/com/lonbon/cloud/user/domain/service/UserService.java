package com.lonbon.cloud.user.domain.service;

import com.lonbon.cloud.user.domain.dto.UserCreateDTO;
import com.lonbon.cloud.user.domain.dto.UserUpdateDTO;
import com.lonbon.cloud.user.domain.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(UserCreateDTO user);
    User updateUser(UUID id, UserUpdateDTO user);
    void deleteUser(UUID id);
    Optional<User> getUserById(UUID id);
    Optional<User> getUserByUsername(String username);
    List<User> getAllUsers();
}
