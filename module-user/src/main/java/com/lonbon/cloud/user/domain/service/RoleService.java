package com.lonbon.cloud.user.domain.service;

import com.lonbon.cloud.base.service.EntityService;
import com.lonbon.cloud.user.domain.entity.Role;
import com.lonbon.cloud.user.domain.entity.proxy.RoleProxy;

import java.util.List;
import java.util.UUID;

public interface RoleService extends EntityService<Role, RoleProxy> {

    List<Role> getRolesByUserId(UUID userId);

}
