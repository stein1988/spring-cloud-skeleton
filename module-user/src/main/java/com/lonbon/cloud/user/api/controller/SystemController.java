package com.lonbon.cloud.user.api.controller;

import com.lonbon.cloud.base.response.Response;
import com.lonbon.cloud.base.service.SystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
@Tag(name = "系统", description = "系统级操作，谨慎使用")
public class SystemController {

    private final SystemService systemService;

    @PostMapping("/sync-table")
    @Operation(summary = "同步表", description = "code first 同步表")
    public Response<Void> syncTable() {
        systemService.syncTable();
        return Response.success();
    }
}
