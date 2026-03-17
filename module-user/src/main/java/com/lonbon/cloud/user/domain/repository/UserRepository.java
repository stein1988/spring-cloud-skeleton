package com.lonbon.cloud.user.domain.repository;

import com.lonbon.cloud.base.repository.Repository;
import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.entity.proxy.UserProxy;

import java.util.UUID;

public interface UserRepository extends Repository<UserProxy, User, UUID> {

}
