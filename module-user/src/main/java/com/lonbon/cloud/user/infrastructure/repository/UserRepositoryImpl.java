package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.repository.UserRepository;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryImpl implements UserRepository {

    @Inject
    private EasyEntityQuery easyEntityQuery;

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            easyEntityQuery.insertable(user);
        } else {
            easyEntityQuery.updatable(user);
        }
        return user;
    }

    @Override
    public void delete(UUID id) {
        easyEntityQuery.deletable(User.class).where(o -> o.id().eq(id));
    }

    @Override
    public Optional<User> findById(UUID id) {
        User user = easyEntityQuery.queryable(User.class).where(o -> o.id().eq(id)).firstOrNull();
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        User user = easyEntityQuery.queryable(User.class).where(o -> o.username().eq(username)).firstOrNull();
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        return easyEntityQuery.queryable(User.class).toList();
    }

    @Override
    public List<User> findByTenantId(UUID tenantId) {
        return easyEntityQuery.queryable(User.class).where(o -> o.currentTenantId().eq(tenantId)).toList();
    }
}
