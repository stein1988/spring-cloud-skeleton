package com.lonbon.cloud.user.application.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.temp.SaTempUtil;
import com.lonbon.cloud.base.satoken.RefreshToken;
import com.lonbon.cloud.user.domain.dto.LoginRequest;
import com.lonbon.cloud.user.domain.dto.LoginResponse;
import com.lonbon.cloud.user.domain.dto.RefreshTokenRequest;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.entity.UserTenant;
import com.lonbon.cloud.user.domain.entity.proxy.UserProxy;
import com.lonbon.cloud.user.domain.service.AuthService;
import com.lonbon.cloud.user.domain.service.TenantService;
import com.lonbon.cloud.user.domain.service.UserService;
import com.lonbon.cloud.user.domain.service.UserTenantService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserService userService;

    @Resource
    private TenantService tenantService;

    @Resource
    private UserTenantService userTenantService;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userService.getEntity(o -> o.username().eq(request.getUsername()))
                               .orElseThrow(() -> new RuntimeException("username not found"));

        // TODO：需要加密验证，目前代码只是跑通流程
        if (!user.getPasswordHash().equals(request.getPasswordCipher()))
            throw new RuntimeException("password not match");

        boolean isTenantAdmin = false;
        if (user.getIsSuperAdmin()) {
            // 超级管理员不需要判定和租户的关系，任何租户都能进入
            // 如果没有当前租户ID，查找最早创建的租户，设置为当前租户，并登录
            if (user.getCurrentTenantId() == null) {
                Tenant tenant = tenantService.getFirstEntity(t -> t.isDefault().eq(true), t -> t.createTime().asc())
                                             .orElseThrow(() -> new RuntimeException("default tenant not found"));

                user.setCurrentTenantId(tenant.getId());
                userService.updateEntity(user, UserProxy::currentTenantId);
            }
        } else {
            // 非超级管理员：需要检查当前租户，如果没有，不给登录
            if (user.getCurrentTenantId() == null) throw new RuntimeException("current tenant id not found");

            Tenant tenant = tenantService.getEntityById(user.getCurrentTenantId())
                                         .orElseThrow(() -> new RuntimeException("tenant not found"));

            Optional<UserTenant> userTenant = userTenantService.getEntity(e -> {
                e.tenantId().eq(user.getCurrentTenantId());
                e.userId().eq(user.getId());
            });

            isTenantAdmin = userTenant.map(UserTenant::getIsTenantAdmin).orElse(false);
        }

        // TODO：将isSuperAdmin和isTenantAdmin存到satoken的缓存系统中
        return createLoginResponse(user, isTenantAdmin);
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

        User user = userService.getEntityById(userId).orElseThrow(() -> new RuntimeException("user not found"));

        boolean isTenantAdmin = false;
        if (user.getIsSuperAdmin()) {
            // 超级管理员不需要判定和租户的关系，任何租户都能进入
            // 如果没有当前租户ID，查找最早创建的租户，设置为当前租户，并登录
            if (user.getCurrentTenantId() == null) {
                Tenant tenant = tenantService.getFirstEntity(t -> t.isDefault().eq(true), t -> t.createTime().asc())
                                             .orElseThrow(() -> new RuntimeException("default tenant not found"));

                user.setCurrentTenantId(tenant.getId());
                userService.updateEntity(user, UserProxy::currentTenantId);
            }
        } else {
            // 非超级管理员：需要检查当前租户，如果没有，不给登录
            if (user.getCurrentTenantId() == null) throw new RuntimeException("current tenant id not found");

            Tenant tenant = tenantService.getEntityById(user.getCurrentTenantId())
                                         .orElseThrow(() -> new RuntimeException("tenant not found"));

            Optional<UserTenant> userTenant = userTenantService.getEntity(e -> {
                e.tenantId().eq(user.getCurrentTenantId());
                e.userId().eq(user.getId());
            });

            isTenantAdmin = userTenant.map(UserTenant::getIsTenantAdmin).orElse(false);
        }

        // 生成token
        return createLoginResponse(user, isTenantAdmin);
    }


    private LoginResponse createLoginResponse(User user, boolean isTenantAdmin) {
        UUID userId = user.getId();
        String userIdStr = userId.toString();
        log.info("login:userId:{}", userIdStr);

        // 生成token，必须加入random随机参数rnd，否则同一用户每次
        StpUtil.login(userIdStr, new SaLoginParameter().setExtra("sub", userIdStr).setExtra("rnd", UUID.randomUUID()));
        SaTokenInfo info = StpUtil.getTokenInfo();
        String token = info.getTokenValue();
        long timeout = info.getTokenTimeout();

        // 生成刷新token
        long refreshTimeout = 10000;
        RefreshToken refreshToken = new RefreshToken(userId, token, refreshTimeout);

        return new LoginResponse(userId, token, timeout, refreshToken.generate(), refreshToken.getExpiresIn());
    }
}
