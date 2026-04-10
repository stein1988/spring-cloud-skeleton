package com.lonbon.cloud.user.application.service;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.lonbon.cloud.base.service.SystemService;
import org.springframework.stereotype.Service;

@Service
public class SystemServiceImpl extends SystemService {
    public SystemServiceImpl(EasyEntityQuery easyEntityQuery) {
        super(easyEntityQuery, "com.lonbon.cloud.user.domain.entity");
    }
}
