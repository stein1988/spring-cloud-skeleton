package com.lonbon.cloud.user.api.controller;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import com.lonbon.cloud.base.response.Response;
import com.lonbon.cloud.user.domain.dto.LoginRequest;
import com.lonbon.cloud.user.domain.dto.LoginResponse;
import com.lonbon.cloud.user.domain.dto.RefreshTokenRequest;
import com.lonbon.cloud.user.domain.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.KeyPair;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "鉴权", description = "鉴权操作")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "登录", description = "用户名密码登陆")
    public Response<LoginResponse> login(@RequestBody @Validated @NotNull LoginRequest request) {
        return Response.success(authService.login(request));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "刷新token")
    public Response<LoginResponse> refreshToken(@RequestBody @Validated @NotNull RefreshTokenRequest request) {
        return Response.success(authService.refreshToken(request));
    }

    @Operation(summary = "获取公钥", description = "获取公钥描述")
    @GetMapping("/public-key")
    public String publicKey() {
        KeyPair pair = SecureUtil.generateKeyPair("SM2");

        // 2. 转换为BC的EC密钥对象（提取纯内容）
        ECPublicKey ecPublicKey = (ECPublicKey) pair.getPublic();
        ECPrivateKey ecPrivateKey = (ECPrivateKey) pair.getPrivate();

        // 3. 提取纯密钥内容（核心）
        // 公钥：X(32字节) + Y(32字节) = 64字节
        byte[] publicKey = ecPublicKey.getQ().getEncoded(true); // 去掉压缩标识
        // 私钥：仅S值，32字节
        byte[] privateKey = ecPrivateKey.getD().toByteArray();

//
//        byte[] privateKey = pair.getPrivate().getEncoded();
//        byte[] publicKey = pair.getPublic().getEncoded();

        log.info("publicKey:{}", publicKey.length);
        log.info("privateKey:{}", privateKey.length);

        String publicStr = HexUtil.encodeHexStr(publicKey);
        String privateStr = HexUtil.encodeHexStr(privateKey);


        log.info("publicStr{}: {}", publicStr.length(), publicStr);
        log.info("privateStr{}: {}", privateStr.length(), privateStr);

        return publicStr;
//        // 1. 生成 SM2 密钥对（推荐方式）
//        KeyPair keyPair = KeyUtil.generateKeyPair("SM2");
//
//        // 2. 转换为 SM2 公私钥对象（方便提取十六进制/Base64 格式）
//        PrivateKey privateKey = keyPair.getPrivate();
//        PublicKey publicKey = keyPair.getPublic();
//
//        // 3. 提取密钥字符串（十六进制，最常用）
//        publicKey.getEncoded()
////        String privateKeyHex = KeyUtil.encodeHex(privateKey.getS()); // 私钥十六进制
////        String publicKeyHex = KeyUtil.encodeHex(publicKey.getQ().getEncoded(false)); // 公钥十六进制
//
//        // 4. 输出密钥对（实际使用请妥善保存，不要硬编码！）
//        System.out.println("=== SM2 密钥对（十六进制）===");
//        System.out.println("私钥（用于签名）：" + privateKeyHex);
//        System.out.println("公钥（用于验签）：" + publicKeyHex);
//
//        // 5. 可选：Base64 格式（适合网络传输/配置文件）
//        String privateKeyBase64 = KeyUtil.encodeBase64(privateKey.getEncoded());
//        String publicKeyBase64 = KeyUtil.encodeBase64(publicKey.getEncoded());
//        System.out.println("\n=== SM2 密钥对（Base64）===");
//        System.out.println("私钥 Base64：" + privateKeyBase64);
//        System.out.println("公钥 Base64：" + publicKeyBase64);
//
//        // 6. 测试：用生成的密钥对做签名+验签（验证密钥有效性）
//        testSignAndVerify(privateKeyHex, publicKeyHex);
    }
}
