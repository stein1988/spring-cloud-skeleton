package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.entity.proxy.UserProxy;
import com.lonbon.cloud.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserRepositoryImpl extends EasyQueryRepository<UserProxy, User, UserProxy.UserProxyFetcher, UUID> implements UserRepository {
    @Autowired
    public UserRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, User.class, proxy -> proxy.FETCHER);
    }
}
