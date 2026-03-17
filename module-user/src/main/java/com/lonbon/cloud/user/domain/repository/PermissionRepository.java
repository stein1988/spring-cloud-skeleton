package com.lonbon.cloud.user.domain.repository;

import com.lonbon.cloud.base.repository.Repository;
import com.lonbon.cloud.user.domain.entity.Permission;
import com.lonbon.cloud.user.domain.entity.proxy.PermissionProxy;

import java.util.UUID;

public interface PermissionRepository extends Repository<PermissionProxy, Permission, UUID> {

}
