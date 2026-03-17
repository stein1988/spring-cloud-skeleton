package com.lonbon.cloud.user.application.service;

import com.lonbon.cloud.user.domain.entity.Team;
import com.lonbon.cloud.user.domain.repository.TeamRepository;
import com.lonbon.cloud.user.domain.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    @Override
    public Team updateTeam(UUID id, Team team) {
        Optional<Team> exists = teamRepository.findById(id);
        if (exists.isPresent()) {
            team.setId(id);
            return teamRepository.save(team);
        } else throw new RuntimeException("Team not exists");
    }

    @Override
    public void deleteTeam(UUID id) {
        teamRepository.deleteById(id);
    }

    @Override
    public Optional<Team> getTeamById(UUID id) {
        return teamRepository.findById(id);
    }

    @Override
    public List<Team> getTeamsByTenantId(UUID tenantId) {
        // TODO: 实现通过租户ID查询团队的功能
        return (List<Team>) teamRepository.findAll();
    }

    @Override
    public Optional<Team> getDefaultTeamByTenantId(UUID tenantId) {
        // TODO: 实现通过租户ID查询默认团队的功能
        return Optional.empty();
    }

    @Override
    public List<Team> getAllTeams() {
        return (List<Team>) teamRepository.findAll();
    }
}
