package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.entity.proxy.UserProxy;
import com.lonbon.cloud.user.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepositoryImpl extends EasyQueryRepository<User, UserProxy, UserProxy.UserProxyFetcher>
        implements UserRepository {

    private static final Map<String, SQLActionExpression2<IncludeContext, UserProxy>> navigateMap = new HashMap<>();

    static {
        navigateMap.put(User.Fields.currentTenant, (c, p) -> {
            c.query(p.currentTenant());
        });
        navigateMap.put(User.Fields.roles, (c, p) -> {
            c.query(p.roles());
        });
    }

    public UserRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, User.class, proxy -> proxy.FETCHER);
    }

    @Override
    protected Map<String, SQLActionExpression2<IncludeContext, UserProxy>> getNavigateMap() {
        return navigateMap;
    }

}
