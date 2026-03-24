package com.lonbon.cloud.user.application.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.lonbon.cloud.user.domain.dto.LoginRequest;
import com.lonbon.cloud.user.domain.dto.LoginResponse;
import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.repository.UserRepository;
import com.lonbon.cloud.user.domain.service.AuthService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 查找username匹配的用户，如果没找到，或者找到多个，都会抛出异常
        User user = userRepository.singleNotNull(o -> o.username().eq(request.getUsername()));
        if (!user.getPasswordHash().equals(request.getPassword_cipher()))
            throw new RuntimeException("password not match");

        StpUtil.login(user.getId());

        SaTokenInfo info = StpUtil.getTokenInfo();

        SaResult.data(info);

        return new LoginResponse(user.getId(), );
    }
}
