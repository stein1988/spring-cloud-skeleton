package com.lonbon.cloud.user.application.service;

import com.lonbon.cloud.base.service.SimpleEntityService;
import com.lonbon.cloud.user.domain.dto.UserCreateDTO;
import com.lonbon.cloud.user.domain.dto.UserUpdateDTO;
import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.entity.proxy.UserProxy;
import com.lonbon.cloud.user.domain.repository.UserRepository;
import com.lonbon.cloud.user.domain.service.UserService;
import io.github.linpeilie.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl extends SimpleEntityService<UserProxy, User, UserRepository> implements UserService {
    @Autowired
    public UserServiceImpl(Converter converter, UserRepository userRepository) {
        super(converter, User.class, userRepository);
    }
}
