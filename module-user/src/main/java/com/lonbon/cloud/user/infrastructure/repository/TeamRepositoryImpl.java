package com.lonbon.cloud.user.infrastructure.repository;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.lonbon.cloud.user.domain.entity.Team;
import com.lonbon.cloud.user.domain.entity.proxy.TeamProxy;
import com.lonbon.cloud.user.domain.repository.TeamRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TeamRepositoryImpl extends EasyQueryRepository<TeamProxy, Team, TeamProxy.TeamProxyFetcher>
        implements TeamRepository {
    
    public TeamRepositoryImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, Team.class, proxy -> proxy.FETCHER);
    }
}
