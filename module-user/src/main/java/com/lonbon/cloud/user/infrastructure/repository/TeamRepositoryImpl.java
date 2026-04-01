package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.lonbon.cloud.user.domain.entity.Team;
import com.lonbon.cloud.user.domain.entity.proxy.TeamProxy;
import com.lonbon.cloud.user.domain.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class TeamRepositoryImpl extends EasyQueryRepository<TeamProxy, Team, TeamProxy.TeamProxyFetcher> implements TeamRepository {
    @Autowired
    public TeamRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, Team.class, proxy -> proxy.FETCHER);
    }
}
