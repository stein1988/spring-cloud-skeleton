package com.lonbon.cloud.user.api.compatible.v2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v2.0/auth")
@Tag(name = "老项目兼容-认证管理", description = "提供老项目认证相关接口")
@Validated
public class CompatibleAuthController {

    @PostMapping("/login")
    @Operation(summary = "账号登录", description = "用户/设备登录认证，支持设备Token认证和APP用户认证两种模式")
    public LegacyResponse<TokenVO> login(@Valid @RequestBody @NotNull AuthLoginRequest request) {
        log.info("登录请求: {}", request);

        if (request.getUserName() == null || request.getUserName().isEmpty()) {
            return LegacyResponse.error("301", "参数错误", null);
        }

        if (request.getAccountType() == null) {
            return LegacyResponse.error("301", "参数错误", null);
        }

        if (request.getAccountType() == 1000) {
            log.info("设备类型登录");
            return handleDeviceLogin(request);
        } else if (request.getAccountType() == 44) {
            log.info("APP类型登录");
            return handleAppLogin(request);
        } else {
            return LegacyResponse.error("201", "不支持的账号类型", null);
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "使用刷新令牌获取新的访问令牌，延长会话有效期")
    public LegacyResponse<TokenVO> refreshToken(@RequestParam String refreshToken) {
        log.info("刷新Token请求, refreshToken长度: {}", refreshToken != null ? refreshToken.length() : 0);

        if (refreshToken == null || refreshToken.isEmpty()) {
            return LegacyResponse.error("201", "刷新令牌不能为空", null);
        }

        String decryptedToken = decryptToken(refreshToken);
        if (decryptedToken == null) {
            return LegacyResponse.error("202", "令牌信息有误", null);
        }

        String mockTokenId = "TODO: 从解析的Token中提取TokenId";
        log.info("解析Token成功, TokenId: {}", mockTokenId);

        boolean tokenExistsInRedis = checkRedisToken(refreshToken);
        if (!tokenExistsInRedis) {
            return LegacyResponse.error("203", "刷新令牌已失效", null);
        }

        boolean tokenExistsInDb = checkDatabaseToken(mockTokenId);
        if (!tokenExistsInDb) {
            return LegacyResponse.error("204", "登陆令牌失效", null);
        }

        TokenVO tokenVO = generateNewToken();
        log.info("生成新Token成功");

        return LegacyResponse.success("200", "刷新成功", tokenVO);
    }

    @PostMapping("/account")
    @Operation(summary = "账号Token查询", description = "查询当前账号的Token信息（预留接口）")
    public LegacyResponse<HashMap<String, Object>> getAccountToken() {
        log.info("账号Token查询请求");

        return LegacyResponse.success("200", "成功", new HashMap<>());
    }

    private LegacyResponse<TokenVO> handleDeviceLogin(AuthLoginRequest request) {
        String mockAccountId = "TODO: 从数据库查询真实账号ID";
        log.info("设备登录，账号ID: {}", mockAccountId);

        boolean accountExists = checkAccountExists(mockAccountId);
        if (!accountExists) {
            return LegacyResponse.error("202", "账号不存在", null);
        }

        boolean hasRole = checkAccountHasRole(mockAccountId);
        if (!hasRole) {
            return LegacyResponse.error("203", "设备未绑定角色", null);
        }

        String oldAccessToken = "TODO: 从Redis删除旧AccessToken";
        String oldRefreshToken = "TODO: 从Redis删除旧RefreshToken";
        log.info("清理旧Token完成");

        deleteOldTokensFromDatabase(mockAccountId);
        log.info("清理数据库旧Token完成");

        TokenVO tokenVO = generateNewToken();
        log.info("生成新Token成功");

        return LegacyResponse.success("200", "登陆成功", tokenVO);
    }

    private LegacyResponse<TokenVO> handleAppLogin(AuthLoginRequest request) {
        log.info("APP用户登录，用户名: {}", request.getUserName());

        String mockAccountId = "TODO: 从数据库查询真实账号ID";
        boolean accountExists = checkAccountExists(mockAccountId);
        if (!accountExists) {
            return LegacyResponse.error("202", "账号不存在", null);
        }

        log.info("APP登录认证成功");
        TokenVO tokenVO = generateNewToken();

        return LegacyResponse.success("200", "登陆成功", tokenVO);
    }

    private TokenVO generateNewToken() {
        String tokenId = UUID.randomUUID().toString().replace("-", "");
        long currentTime = Instant.now().toEpochMilli();

        String accessTokenData = String.format(
                "{\"tokenId\":\"%s\",\"timestamp\":%d,\"type\":\"access\"}",
                tokenId, currentTime
        );
        String refreshTokenData = String.format(
                "{\"tokenId\":\"%s\",\"timestamp\":%d,\"type\":\"refresh\",\"fresh_timestamp\":%d}",
                tokenId, currentTime, currentTime
        );

        String accessToken = encryptToken(accessTokenData);
        String refreshToken = encryptToken(refreshTokenData);

        long expireTime = 7776000L;

        return new TokenVO()
                .setAccessToken(accessToken)
                .setExpireTime(expireTime)
                .setRefreshToken(refreshToken);
    }

    private String encryptToken(String data) {
        String mockEncrypted = Base64.getEncoder().encodeToString(data.getBytes());
        return "RSA_" + mockEncrypted;
    }

    private String decryptToken(String encryptedToken) {
        try {
            if (encryptedToken.startsWith("RSA_")) {
                String base64Part = encryptedToken.substring(4);
                return new String(Base64.getDecoder().decode(base64Part));
            }
            return encryptedToken;
        } catch (Exception e) {
            log.error("Token解密失败", e);
            return null;
        }
    }

    private boolean checkAccountExists(String accountId) {
        log.info("TODO: 检查账号是否存在，账号ID: {}", accountId);
        return true;
    }

    private boolean checkAccountHasRole(String accountId) {
        log.info("TODO: 检查账号是否绑定角色，账号ID: {}", accountId);
        return true;
    }

    private void deleteOldTokensFromDatabase(String accountId) {
        log.info("TODO: 删除数据库中的旧Token记录，账号ID: {}", accountId);
    }

    private boolean checkRedisToken(String token) {
        log.info("TODO: 检查Redis中的Token是否存在");
        return true;
    }

    private boolean checkDatabaseToken(String tokenId) {
        log.info("TODO: 检查数据库中的Token记录是否存在，TokenId: {}", tokenId);
        return true;
    }
}
