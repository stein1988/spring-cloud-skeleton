package com.lonbon.cloud.user.application.controller;

import com.lonbon.cloud.common.utils.Response;
import com.lonbon.cloud.user.domain.entity.Team;
import com.lonbon.cloud.user.domain.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/teams")
@Tag(name = "团队", description = "团队操作")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping
    @Operation(summary = "创建", description = "创建团队")
    public Response<UUID> create(@RequestBody @Validated Team team) {
        Team createdTeam = teamService.createTeam(team);
        return Response.success(createdTeam.getId(), "Team created successfully");
    }

    @PostMapping("/{id}/delete")
    @Operation(summary = "删除", description = "删除团队")
    public Response<UUID> delete(@PathVariable("id") UUID id) {
        teamService.deleteTeam(id);
        return Response.success(id, "Team deleted successfully");
    }

    @PostMapping("/{id}/update")
    @Operation(summary = "更新", description = "更新团队")
    public Response<UUID> update(@PathVariable("id") UUID id, @RequestBody @Validated Team team) {
        teamService.updateTeam(id, team);
        return Response.success(id, "Team updated successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取", description = "获取团队")
    public Response<Team> getTeamById(@PathVariable("id") UUID id) {
        Optional<Team> team = teamService.getTeamById(id);
        return team.map(Response::success).orElseGet(() -> Response.error("Team not found"));
    }

    @GetMapping
    @Operation(summary = "查询所有", description = "查询所有团队")
    public Response<List<Team>> getAllTeams() {
        List<Team> teams = teamService.getAllTeams();
        return Response.success(teams);
    }
}
