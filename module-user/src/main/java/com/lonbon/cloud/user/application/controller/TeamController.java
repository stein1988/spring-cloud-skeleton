package com.lonbon.cloud.user.application.controller;

import com.lonbon.cloud.common.utils.Response;
import com.lonbon.cloud.user.domain.entity.Team;
import com.lonbon.cloud.user.domain.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping
    public Response<Team> createTeam(@RequestBody Team team) {
        try {
            Team createdTeam = teamService.createTeam(team);
            return Response.success(createdTeam, "Team created successfully");
        } catch (Exception e) {
            return Response.error("Failed to create team: " + e.getMessage());
        }
    }

    @PutMapping
    public Response<Team> updateTeam(@RequestBody Team team) {
        try {
            Team updatedTeam = teamService.updateTeam(team);
            return Response.success(updatedTeam, "Team updated successfully");
        } catch (Exception e) {
            return Response.error("Failed to update team: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Response<Void> deleteTeam(@PathVariable("id") String id) {
        try {
            teamService.deleteTeam(UUID.fromString(id));
            return Response.success(null, "Team deleted successfully");
        } catch (Exception e) {
            return Response.error("Failed to delete team: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Response<Team> getTeamById(@PathVariable("id") String id) {
        try {
            Optional<Team> team = teamService.getTeamById(UUID.fromString(id));
            if (team.isPresent()) {
                return Response.success(team.get());
            } else {
                return Response.error("Team not found");
            }
        } catch (Exception e) {
            return Response.error("Failed to get team: " + e.getMessage());
        }
    }

    @GetMapping("/name/{name}/tenant/{tenantId}")
    public Response<Team> getTeamByNameAndTenantId(@PathVariable("name") String name, @PathVariable("tenantId") String tenantId) {
        try {
            Optional<Team> team = teamService.getTeamByNameAndTenantId(name, UUID.fromString(tenantId));
            if (team.isPresent()) {
                return Response.success(team.get());
            } else {
                return Response.error("Team not found");
            }
        } catch (Exception e) {
            return Response.error("Failed to get team: " + e.getMessage());
        }
    }

    @GetMapping
    public Response<List<Team>> getAllTeams() {
        try {
            List<Team> teams = teamService.getAllTeams();
            return Response.success(teams);
        } catch (Exception e) {
            return Response.error("Failed to get teams: " + e.getMessage());
        }
    }

    @GetMapping("/tenant/{tenantId}")
    public Response<List<Team>> getTeamsByTenantId(@PathVariable("tenantId") String tenantId) {
        try {
            List<Team> teams = teamService.getTeamsByTenantId(UUID.fromString(tenantId));
            return Response.success(teams);
        } catch (Exception e) {
            return Response.error("Failed to get teams: " + e.getMessage());
        }
    }

    @GetMapping("/default/tenant/{tenantId}")
    public Response<Team> getDefaultTeamByTenantId(@PathVariable("tenantId") String tenantId) {
        try {
            Optional<Team> team = teamService.getDefaultTeamByTenantId(UUID.fromString(tenantId));
            if (team.isPresent()) {
                return Response.success(team.get());
            } else {
                return Response.error("Default team not found");
            }
        } catch (Exception e) {
            return Response.error("Failed to get default team: " + e.getMessage());
        }
    }

    @PutMapping("/default/{id}/tenant/{tenantId}")
    public Response<Void> setDefaultTeam(@PathVariable("id") String id, @PathVariable("tenantId") String tenantId) {
        try {
            teamService.setDefaultTeam(UUID.fromString(id), UUID.fromString(tenantId));
            return Response.success(null, "Default team set successfully");
        } catch (Exception e) {
            return Response.error("Failed to set default team: " + e.getMessage());
        }
    }
}
