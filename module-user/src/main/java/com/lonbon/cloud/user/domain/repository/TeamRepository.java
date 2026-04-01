package com.lonbon.cloud.user.domain.repository;

import com.lonbon.cloud.base.repository.Repository;
import com.lonbon.cloud.user.domain.entity.Team;
import com.lonbon.cloud.user.domain.entity.proxy.TeamProxy;

import java.util.UUID;

public interface TeamRepository extends Repository<TeamProxy, Team> {

}
