package com.lonbon.cloud.user.domain.repository;

import com.lonbon.cloud.user.domain.entity.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository {
    Team save(Team team);
    void delete(UUID id);
    Optional<Team> findById(UUID id);
    Optional<Team> findByNameAndTenantId(String name, UUID tenantId);
    List<Team> findAll();
    List<Team> findByTenantId(UUID tenantId);
    Optional<Team> findDefaultTeamByTenantId(UUID tenantId);
}
