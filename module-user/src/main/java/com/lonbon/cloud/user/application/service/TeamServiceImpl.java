package com.lonbon.cloud.user.application.service;

import com.lonbon.cloud.user.domain.entity.Team;
import com.lonbon.cloud.user.domain.repository.TeamRepository;
import com.lonbon.cloud.user.domain.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public Team createTeam(Team team) {
        if (team.getId() == null) {
            team.setId(UUID.randomUUID());
        }
        team.setCreatedAt(OffsetDateTime.now());
        team.setUpdatedAt(OffsetDateTime.now());
        team.setVersionId(0);
        team.setDeleted(false);
        return teamRepository.save(team);
    }

    @Override
    public Team updateTeam(Team team) {
        team.setUpdatedAt(OffsetDateTime.now());
        team.setVersionId(team.getVersionId() + 1);
        return teamRepository.save(team);
    }

    @Override
    public void deleteTeam(UUID id) {
        teamRepository.delete(id);
    }

    @Override
    public Optional<Team> getTeamById(UUID id) {
        return teamRepository.findById(id);
    }

    @Override
    public Optional<Team> getTeamByNameAndTenantId(String name, UUID tenantId) {
        return teamRepository.findByNameAndTenantId(name, tenantId);
    }

    @Override
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @Override
    public List<Team> getTeamsByTenantId(UUID tenantId) {
        return teamRepository.findByTenantId(tenantId);
    }

    @Override
    public Optional<Team> getDefaultTeamByTenantId(UUID tenantId) {
        return teamRepository.findDefaultTeamByTenantId(tenantId);
    }

    @Override
    public void setDefaultTeam(UUID teamId, UUID tenantId) {
        // 先将该租户下所有团队的isDefault设置为false
        List<Team> teams = teamRepository.findByTenantId(tenantId);
        for (Team team : teams) {
            team.setDefault(team.getId().equals(teamId));
            team.setUpdatedAt(OffsetDateTime.now());
            teamRepository.save(team);
        }
    }
}
