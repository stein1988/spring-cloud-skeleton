package com.lonbon.cloud.base.satoken;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.jwt.SaJwtTemplate;
import cn.dev33.satoken.jwt.SaJwtUtil;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.jwt.error.SaJwtErrorCode;
import cn.dev33.satoken.jwt.exception.SaJwtException;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.temp.SaTempTemplate;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTException;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 重载SaToken的Configuration，完成以下功能：
 * 1、将默认token改为jwt样式
 * 2、使用国密SM4算法加密jwt，代替默认的HS256算法
 */

@Configuration
public class SaTokenConfiguration {

    private final StpLogicJwtForSimple stpLogicJwt = new StpLogicJwtForSimple() {

        /**
         * 获取jwt秘钥，确保是合法的SM4密钥
         * 先使用super.jwtSecretKey()获取配置sa-token.jwt-secret-key，然后确认格式必须是32个16进制字符
         * @return /
         */
        @Override
        public String jwtSecretKey() {

            String keyt = super.jwtSecretKey();

            // 必须是 32 个字符（16个字节 = 32位十六进制）
            if (keyt == null || keyt.length() != 32)
                throw (new SaJwtException("jwt秘钥必须是 32 个十六进制字符")).setCode(30205);

            // 必须是合法的 16 进制字符（0-9, a-f, A-F）
            if (!keyt.matches("^[0-9a-fA-F]+$"))
                throw (new SaJwtException("jwt秘钥必须是十六进制字符！只能包含 0-9、a-f、A-F")).setCode(30205);

            return keyt;
        }
    };


    /**
     * Sa-Token 整合 jwt (Simple 简单模式)
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return stpLogicJwt;
    }

    /**
     * 自定义 SaJwtUtil 生成 token 的算法，将默认的HS256，替换为国密
     * PostConstruct注解，作用是：在 Bean 初始化完成后，自动执行一次这个方法。
     */
    @PostConstruct
    public void setSaJwtTemplate() {
        SaJwtUtil.setSaJwtTemplate(new SaJwtTemplate() {

            /**
             * 返回 jwt 使用的签名算法，替换成SM4加密签名算法
             *
             * @param keyt 秘钥
             * @return /
             */
            @Override
            public JWTSigner createSigner(String keyt) {
                byte[] keyBytes = HexUtil.decodeHex(keyt);
                SecretKey sm4Key = new SecretKeySpec(keyBytes, "SM4");
                return JWTSignerUtil.sm4cmac(sm4Key);
            }

            /**
             * 创建 jwt （简单方式）
             *
             * @param loginType 登录类型
             * @param loginId 账号id
             * @param extraData 扩展数据
             * @param keyt 秘钥
             * @return jwt-token
             */
            @Override
            public String createToken(String loginType, Object loginId, Map<String, Object> extraData, String keyt) {

                Object deviceType = extraData.remove(JWTUtil.DEVICE_TYPE);
                Object timeout = extraData.remove(JWTUtil.TIMEOUT);

                return createToken(loginType, loginId, deviceType instanceof String s ? s : "",
                                   timeout instanceof Number n ? n.longValue() : 0L, extraData, keyt);
            }

            /**
             * 创建 jwt （全参数方式）
             *
             * @param loginType 账号类型
             * @param loginId 账号id
             * @param deviceType 设备类型
             * @param timeout token有效期 (单位 秒)
             * @param extraData 扩展数据
             * @param keyt 秘钥
             * @return jwt-token
             */
            @Override
            public String createToken(
                    String loginType, Object loginId, String deviceType, long timeout,
                    Map<String, Object> extraData, String keyt) {

                String sub = loginId instanceof UUID ? loginId.toString().replace("-", "") : loginId.toString();

                // 计算 eff 有效期：
                // 如果 timeout 小于等于0，那么 eff 为 -1，代表永不过期
                // 如果 timeout 指定为一个具体的值，那么 eff 为 13 位时间戳，代表此 token 到期的时间
                long effTime = timeout > 0 ? timeout * 1000 + System.currentTimeMillis() : NEVER_EXPIRE;

                JWT jwt = JWT.create().setJWTId(JWTUtil.generateJti()).setSubject(sub)
                             .setPayload(JWTUtil.EXPIRATION_TIME, effTime).setPayload(JWTUtil.LOGIN_TYPE, loginType)
                             .setPayload(JWTUtil.DEVICE_TYPE, deviceType).addPayloads(extraData);

                // 返回
                return generateToken(jwt, keyt);
            }


            /**
             * jwt 解析
             *
             * @param token Jwt-Token值
             * @param loginType 登录类型
             * @param keyt 秘钥
             * @param isCheckTimeout 是否校验 timeout 字段
             * @return 解析后的jwt 对象
             */
            @Override
            public JWT parseToken(String token, String loginType, String keyt, boolean isCheckTimeout) {

                // 秘钥不可以为空
                if (SaFoxUtil.isEmpty(keyt)) {
                    throw new SaJwtException("请配置 jwt 秘钥");
                }

                // 解析
                JWT jwt;
                try {
                    jwt = JWT.of(token);
                } catch (JWTException | JSONException e) {
                    throw new SaJwtException("jwt 解析失败：" + token, e).setCode(SaJwtErrorCode.CODE_30201);
                }
                JSONObject payloads = jwt.getPayloads();

                // 校验 Token 签名
                boolean verify = jwt.setSigner(createSigner(keyt)).verify();
                if (!verify) {
                    throw new SaJwtException("jwt 签名无效：" + token).setCode(SaJwtErrorCode.CODE_30202);
                }

                // 校验 loginType
                if (!Objects.equals(loginType, payloads.getStr(JWTUtil.LOGIN_TYPE))) {
                    throw new SaJwtException("jwt loginType 无效：" + token).setCode(SaJwtErrorCode.CODE_30203);
                }

                // 校验 Token 有效期
                if (isCheckTimeout) {
                    Long effTime = payloads.getLong(JWTUtil.EXPIRATION_TIME, 0L);
                    if (effTime != NEVER_EXPIRE) {
                        if (effTime < System.currentTimeMillis()) {
                            throw new SaJwtException("jwt 已过期：" + token).setCode(SaJwtErrorCode.CODE_30204);
                        }
                    }
                }

                // 返回
                return jwt;
            }
        });
    }

    /**
     * 自定义 Temp Token 的生成和解析算法，改成 jwt ，来生成refresh token
     */
    @PostConstruct
    public void setSaTempTemplate() {
        SaManager.setSaTempTemplate(new SaTempTemplate() {

            /**
             * 随机一个 temp-token
             *
             * @return /
             */
            @Override
            public String randomTempToken(Object value) {
                // 如果传入 RefreshToken，则由RefreshToken提供jwt数据，只提供自校验数据，缩短长度
                if (value instanceof UserRefreshToken token)
                    return SaJwtUtil.generateToken(token.getJwt(), stpLogicJwt.jwtSecretKey());

                    // 如果传入 JWT，则生成jwt token
                else if (value instanceof JWT jwt) return SaJwtUtil.generateToken(jwt, stpLogicJwt.jwtSecretKey());
                else return super.randomTempToken(value);
            }

//            /**
//             * 解析 token 获取 value，并裁剪指定前缀，然后转换为指定类型
//             * @param token 指定 Token
//             * @param cs 指定类型
//             * @param <T> 默认值的类型
//             * @return /
//             */
//            @Override
//            public<T> T parseToken(String token, String cutPrefix, Class<T> cs) {
//                if (cs == JWT.class) {
//                    Object value = parseToken(token);
//                    return (T) value;
//                } else {
//                    return super.parseToken(token, cutPrefix, cs);
//                }
//            }
        });
    }
}
