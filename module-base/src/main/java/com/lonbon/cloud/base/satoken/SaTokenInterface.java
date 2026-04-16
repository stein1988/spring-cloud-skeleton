package com.lonbon.cloud.base.satoken;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.lonbon.cloud.base.dto.LoginUser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SaTokenInterface implements StpInterface {

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        LoginUser loginUser = StpUtil.getTokenSession().get(LoginUser.KEY, new LoginUser());
        return loginUser.getRoleCodes();
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of();
    }

}
