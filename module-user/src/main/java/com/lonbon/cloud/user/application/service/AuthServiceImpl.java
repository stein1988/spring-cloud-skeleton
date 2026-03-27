package com.lonbon.cloud.user.application.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.temp.SaTempUtil;

import com.lonbon.cloud.base.satoken.RefreshToken;
import com.lonbon.cloud.user.domain.dto.LoginRequest;
import com.lonbon.cloud.user.domain.dto.LoginResponse;
import com.lonbon.cloud.user.domain.dto.RefreshTokenRequest;
import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.repository.UserRepository;
import com.lonbon.cloud.user.domain.service.AuthService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 查找username匹配的用户，如果没找到，或者找到多个，都会抛出异常
//        User user = userRepository.singleNotNull(o -> o.username().eq(request.getUsername()));
//        if (!user.getPasswordHash().equals(request.getPasswordCipher()))
//            throw new RuntimeException("password not match");

        User user = new User();
        user.setId(UUID.randomUUID());

        return createLoginResponse(user);
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshTokenStr = request.getRefreshToken();
        RefreshToken refreshToken = RefreshToken.parse(refreshTokenStr);
        if (refreshToken == null) {
            throw new RuntimeException("refreshToken not valid");
        }
        UUID userId = refreshToken.getUserId();
        if (userId == null) {
            throw new RuntimeException("refreshToken not valid");
        }
        
        // 注销旧的token
        StpUtil.logoutByTokenValue(refreshToken.getAccessToken());

        // 删除旧的refresh token
        SaTempUtil.deleteToken(refreshTokenStr);

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("user not found");
        }

        // 生成token
        return createLoginResponse(user);
    }

    private LoginResponse createLoginResponse(User user) {
        UUID userId = user.getId();
        String userIdStr = userId.toString();
        log.info("login:userId:{}", userIdStr);

        // 生成token
        StpUtil.login(userIdStr, new SaLoginParameter().setExtra("uid", userIdStr));
        SaTokenInfo info = StpUtil.getTokenInfo();
        String token = info.getTokenValue();
        long timeout = info.getTokenTimeout();

        // 生成刷新token
        long refreshTimeout = 10000;
        RefreshToken refreshToken = new RefreshToken(userId, token, refreshTimeout);

        return new LoginResponse(userId, token, timeout, refreshToken.generate(), refreshToken.getExpiresIn());
    }
}
