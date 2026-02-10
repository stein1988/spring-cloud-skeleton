package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.repository.TenantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
public class TenantRepositoryImpl implements TenantRepository {

    @Autowired
    private EasyEntityQuery easyEntityQuery;

    @Override
    public Tenant save(Tenant tenant) {
        log.info("Saving tenant {}", tenant);
        long rows = easyEntityQuery.insertable(tenant).executeRows();
        log.info("rows : {}", rows);
        return tenant;
    }

    @Override
    public void delete(UUID id) {
        easyEntityQuery.deletable(Tenant.class).where(o -> o.id().eq(id));
    }

    @Override
    public Optional<Tenant> findById(UUID id) {
        Tenant tenant = easyEntityQuery.queryable(Tenant.class).where(o -> o.id().eq(id)).firstOrNull();
        return Optional.ofNullable(tenant);
    }

    @Override
    public Optional<Tenant> findByName(String name) {
        Tenant tenant = easyEntityQuery.queryable(Tenant.class).where(o -> o.name().eq(name)).firstOrNull();
        return Optional.ofNullable(tenant);
    }

    @Override
    public List<Tenant> findAll() {
        return easyEntityQuery.queryable(Tenant.class).toList();
    }

    @Override
    public Optional<Tenant> findDefaultTenant() {
        Tenant tenant = easyEntityQuery.queryable(Tenant.class).where(o -> o.isDefault().eq(true)).firstOrNull();
        return Optional.ofNullable(tenant);
    }
}
