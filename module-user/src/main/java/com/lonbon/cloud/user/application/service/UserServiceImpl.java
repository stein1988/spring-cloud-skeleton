package com.lonbon.cloud.user.application.service;

import com.lonbon.cloud.base.service.EntityServiceImpl;
import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.entity.proxy.UserProxy;
import com.lonbon.cloud.user.domain.repository.UserRepository;
import com.lonbon.cloud.user.domain.service.UserService;
import io.github.linpeilie.Converter;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends EntityServiceImpl<User, UserProxy> implements UserService {
    public UserServiceImpl(Converter converter, UserRepository repository) {
        super(converter, repository, User.class);
    }
}
