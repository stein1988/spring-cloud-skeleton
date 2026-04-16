package com.lonbon.cloud.user.domain.service;

import com.lonbon.cloud.base.service.Service;
import com.lonbon.cloud.user.domain.entity.Role;
import com.lonbon.cloud.user.domain.entity.proxy.RoleProxy;

import java.util.List;
import java.util.UUID;

public interface RoleService extends Service<Role, RoleProxy> {

    List<Role> getRolesByUserId(UUID userId);

}
