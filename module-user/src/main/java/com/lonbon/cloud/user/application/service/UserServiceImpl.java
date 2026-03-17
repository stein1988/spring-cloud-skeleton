package com.lonbon.cloud.user.application.service;

import com.lonbon.cloud.user.domain.dto.UserCreateDTO;
import com.lonbon.cloud.user.domain.dto.UserUpdateDTO;
import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.repository.UserRepository;
import com.lonbon.cloud.user.domain.service.UserService;
import io.github.linpeilie.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Converter converter;

    @Override
    public User createUser(UserCreateDTO user) {
        User createUser = converter.convert(user, User.class);
        return userRepository.save(createUser);
    }

    @Override
    public User updateUser(UUID id, UserUpdateDTO user) {
        Optional<User> exists = userRepository.findById(id);
        if (exists.isPresent()) {
            User update = converter.convert(user, exists.get());
            return userRepository.save(update);
        } else throw new RuntimeException("User not exists");
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        // TODO: 实现通过用户名查询用户的功能
        return Optional.empty();
    }

    @Override
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }
}
