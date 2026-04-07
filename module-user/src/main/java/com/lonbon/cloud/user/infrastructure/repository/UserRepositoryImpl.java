package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.entity.proxy.UserProxy;
import com.lonbon.cloud.user.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl extends EasyQueryRepository<UserProxy, User, UserProxy.UserProxyFetcher>
        implements UserRepository {
    
    public UserRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, User.class, proxy -> proxy.FETCHER);
    }
}
