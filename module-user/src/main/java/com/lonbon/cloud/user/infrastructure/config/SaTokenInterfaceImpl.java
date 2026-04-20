package com.lonbon.cloud.user.infrastructure.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.lonbon.cloud.base.satoken.LoginUser;
import com.lonbon.cloud.user.domain.service.PermissionService;
import com.lonbon.cloud.user.domain.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SaTokenInterfaceImpl implements StpInterface {

    @Resource
    private RoleService roleService;

    @Resource
    private PermissionService permissionService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        LoginUser loginUser = StpUtil.getSessionByLoginId(loginId).get(LoginUser.KEY, new LoginUser());
        return loginUser.getRoleCodes();
    }
}
