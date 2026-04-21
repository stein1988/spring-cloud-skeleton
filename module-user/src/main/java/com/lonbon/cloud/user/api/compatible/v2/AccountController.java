package com.lonbon.cloud.user.api.compatible.v2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v2.0/accounts")
@Tag(name = "老项目兼容-账号管理", description = "提供老项目账号注册和校验接口")
public class AccountController {

    @PostMapping
    @Operation(summary = "账号注册", description = "创建新账号，支持设备账号和普通用户账号的注册")
    public LegacyResponse<Map<String, Object>> createAccount(@Valid @RequestBody AccountCreateRequest request) {
        log.info("账号注册请求: {}", request);

        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            return LegacyResponse.error("301", "参数错误", new HashMap<>());
        }

        if (request.getAccountType() == null) {
            return LegacyResponse.error("301", "参数错误", new HashMap<>());
        }

        String mockAccountId = generateAccountId();
        log.info("生成账号ID: {}", mockAccountId);

        if (request.getAccountType() == 1000 || request.getAccountType() == 1100) {
            if (request.getDeviceObj() != null && request.getDeviceObj().getDeviceCategory() != null) {
                log.info("设备类型注册，设备类别: {}", request.getDeviceObj().getDeviceCategory());
            }
        }

        if (request.getAccountType() == 44) {
            log.info("APP类型账号注册，当前实现返回失败");
            return LegacyResponse.error("201", "当前账号类型暂不支持", new HashMap<>());
        }

        return LegacyResponse.success("200", "注册成功", new HashMap<>());
    }

    @PostMapping("/verify")
    @Operation(summary = "校验账号信息", description = "校验账号信息是否存在，根据账号类型和账号值查询账号详情")
    public LegacyResponse<AccountResponse> verifyAccount(@Valid @RequestBody AccountVerifyRequest request) {
        log.info("校验账号请求: {}", request);

        if (request.getAccountValue() == null || request.getAccountValue().isEmpty()) {
            return LegacyResponse.error("301", "参数错误", null);
        }

        if (request.getAccountType() == null) {
            return LegacyResponse.error("301", "参数错误", null);
        }

        String mockAccountId = "TODO: 从数据库查询真实账号ID";
        String mockUsername = request.getAccountValue();

        if (request.getAccountType() == 1000 || request.getAccountType() == 1100) {
            log.info("设备类型账号查询，使用username字段查询");
        } else {
            log.info("其他类型账号查询，使用accountId字段查询");
        }

        AccountVO accountVO = new AccountVO().setAccountId(mockAccountId).setUsername(mockUsername)
                                             .setAccountType(request.getAccountType());

        AccountResponse response = new AccountResponse();
        response.setAccount(accountVO);

        return LegacyResponse.success("200", "查询成功", response);
    }

    private String generateAccountId() {
        return UUID.randomUUID().toString().replace("-", "") + "_" + Instant.now().toEpochMilli();
    }
}
