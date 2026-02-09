package com.lonbon.cloud.user.application.controller;

import com.lonbon.cloud.common.utils.Response;
import com.lonbon.cloud.user.domain.entity.Team;
import com.lonbon.cloud.user.domain.service.TeamService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Delete;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.annotation.Put;
import org.noear.solon.annotation.Path;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@Mapping("/api/teams")
public class TeamController {

    @Inject
    private TeamService teamService;

    @Post
    @Mapping
    public Response<Team> createTeam(Team team) {
        try {
            Team createdTeam = teamService.createTeam(team);
            return Response.success(createdTeam, "Team created successfully");
        } catch (Exception e) {
            return Response.error("Failed to create team: " + e.getMessage());
        }
    }

    @Put
    @Mapping
    public Response<Team> updateTeam(Team team) {
        try {
            Team updatedTeam = teamService.updateTeam(team);
            return Response.success(updatedTeam, "Team updated successfully");
        } catch (Exception e) {
            return Response.error("Failed to update team: " + e.getMessage());
        }
    }

    @Delete
    @Mapping("/{id}")
    public Response<Void> deleteTeam(@Path("id") String id) {
        try {
            teamService.deleteTeam(UUID.fromString(id));
            return Response.success(null, "Team deleted successfully");
        } catch (Exception e) {
            return Response.error("Failed to delete team: " + e.getMessage());
        }
    }

    @Get
    @Mapping("/{id}")
    public Response<Team> getTeamById(@Path("id") String id) {
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

    @Get
    @Mapping("/name/{name}/tenant/{tenantId}")
    public Response<Team> getTeamByNameAndTenantId(@Path("name") String name, @Path("tenantId") String tenantId) {
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

    @Get
    @Mapping
    public Response<List<Team>> getAllTeams() {
        try {
            List<Team> teams = teamService.getAllTeams();
            return Response.success(teams);
        } catch (Exception e) {
            return Response.error("Failed to get teams: " + e.getMessage());
        }
    }

    @Get
    @Mapping("/tenant/{tenantId}")
    public Response<List<Team>> getTeamsByTenantId(@Path("tenantId") String tenantId) {
        try {
            List<Team> teams = teamService.getTeamsByTenantId(UUID.fromString(tenantId));
            return Response.success(teams);
        } catch (Exception e) {
            return Response.error("Failed to get teams: " + e.getMessage());
        }
    }

    @Get
    @Mapping("/default/tenant/{tenantId}")
    public Response<Team> getDefaultTeamByTenantId(@Path("tenantId") String tenantId) {
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

    @Put
    @Mapping("/default/{id}/tenant/{tenantId}")
    public Response<Void> setDefaultTeam(@Path("id") String id, @Path("tenantId") String tenantId) {
        try {
            teamService.setDefaultTeam(UUID.fromString(id), UUID.fromString(tenantId));
            return Response.success(null, "Default team set successfully");
        } catch (Exception e) {
            return Response.error("Failed to set default team: " + e.getMessage());
        }
    }
}
