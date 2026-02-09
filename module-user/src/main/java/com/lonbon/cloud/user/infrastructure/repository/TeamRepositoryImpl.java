package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.user.domain.entity.Team;
import com.lonbon.cloud.user.domain.repository.TeamRepository;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TeamRepositoryImpl implements TeamRepository {

    @Inject
    private EasyEntityQuery easyEntityQuery;

    @Override
    public Team save(Team team) {
        if (team.getId() == null) {
            easyEntityQuery.insertable(team);
        } else {
            easyEntityQuery.updatable(team);
        }
        return team;
    }

    @Override
    public void delete(UUID id) {
        easyEntityQuery.deletable(Team.class).where(o -> o.id().eq(id));
    }

    @Override
    public Optional<Team> findById(UUID id) {
        Team team = easyEntityQuery.queryable(Team.class).where(o -> o.id().eq(id)).firstOrNull();
        return Optional.ofNullable(team);
    }

    @Override
    public Optional<Team> findByNameAndTenantId(String name, UUID tenantId) {
        Team team = easyEntityQuery.queryable(Team.class)
                .where(o -> {
                    o.name().eq(name);
                    o.tenantId().eq(tenantId);
                })
                .firstOrNull();
        return Optional.ofNullable(team);
    }

    @Override
    public List<Team> findAll() {
        return easyEntityQuery.queryable(Team.class).toList();
    }

    @Override
    public List<Team> findByTenantId(UUID tenantId) {
        return easyEntityQuery.queryable(Team.class).where(o -> o.tenantId().eq(tenantId)).toList();
    }

    @Override
    public Optional<Team> findDefaultTeamByTenantId(UUID tenantId) {
        Team team = easyEntityQuery.queryable(Team.class)
                .where(o -> {
                    o.tenantId().eq(tenantId);
                    o.isDefault().eq(true);
                })
                .firstOrNull();
        return Optional.ofNullable(team);
    }
}
