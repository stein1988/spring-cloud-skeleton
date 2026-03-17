package com.lonbon.cloud.user.domain.service;

import com.lonbon.cloud.user.domain.entity.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamService {
    Team createTeam(Team team);
    Team updateTeam(UUID id, Team team);
    void deleteTeam(UUID id);
    Optional<Team> getTeamById(UUID id);
    List<Team> getTeamsByTenantId(UUID tenantId);
    Optional<Team> getDefaultTeamByTenantId(UUID tenantId);
    List<Team> getAllTeams();
}
