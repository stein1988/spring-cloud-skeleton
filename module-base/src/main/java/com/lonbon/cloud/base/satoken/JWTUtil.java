package com.lonbon.cloud.base.satoken;

import java.security.SecureRandom;
import java.util.Base64;

public class JWTUtil {
    public static final String JWT_ID = "jti";
    /**
     * 主题/用户唯一标识，存用户ID
     */
    public static final String SUBJECT = "sub";
    public static final String AUDIENCE = "aud";
    public static final String ISSUED_AT = "iat";
    public static final String EXPIRATION_TIME = "exp";

    public static final String TIMEOUT = "eff";
    public static final String LOGIN_TYPE = "lgt";
    public static final String DEVICE_TYPE = "dvt";

    public static final String CURRENT_TENANT_ID = "cti";
    public static final String IS_SUPER_ADMIN = "sua";
    public static final String IS_TENANT_ADMIN = "tna";

    public static String generateJti() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}


/*
  # JWT Token 完整规范（生产级标准，适配 SpringBoot4 + Sa-Token 技术栈）
  ## 一、基础结构规范（标准 JWT 三段式）
  JWT 固定由 **Header.Payload.Signature** 三部分组成，`.` 分隔，**无空格、无换行、纯小写Base64URL**
  ```
  eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTYiLCJleSI6MTY5...xxxx.abc123sign
  ```
  <p>
  ### 1. Header 头部（算法+类型）
  **必填字段**
  - `alg`：签名算法，生产推荐
  - 对称加密：`HS256`(HMAC-SHA256) 最常用
  - 非对称：`RS256`/`ES256`（分布式、微服务推荐）
  - `typ`：固定 `JWT`
  <p>
  **禁止**：弱算法 `none`、`HS512` 非必要不使用
  <p>
  ---
  <p>
  ### 2. Payload 载荷（核心规范，分标准声明+业务声明）
  #### 🔹 标准注册声明（建议强制使用）
  | 字段 | 含义 | 规范要求 |
  |------|------|----------|
  | `sub` | 主题/用户唯一标识 | 存用户ID，**必选** |
  | `iss` | 签发人 | 服务名/项目域名，如 `https://api.lonbon.com` |
  | `aud` | 接收方 | 客户端标识/前端应用ID |
  | `iat` | 签发时间(时间戳) | 令牌颁发时间 |
  | `exp` | 过期时间(时间戳) | **必选**，绝对过期 |
  | `nbf` | 生效开始时间 | 防提前使用，可选 |
  | `jti` | JWT唯一编号 | 用于注销、黑名单、防重放，**建议必加** |

  字段	英文全称	中文含义
    sub	Subject	主题 / 用户唯一标识
    iss	Issuer	签发人、颁发者
    aud	Audience	接收方、受众
    iat	Issued At	签发时间
    exp	Expiration Time	过期时间
    nbf	Not Before	生效起始时间
    jti	JWT ID	JWT 唯一标识
  <p>
  #### 🔹 自定义业务声明（统一约束）
  1. 字段**小写驼峰/下划线**统一风格，禁止拼音、乱命名
  2. 敏感数据**严禁放入**：密码、手机号、身份证、权限明文
  3. 只存**轻量只读数据**：
  - `userId`：用户主键
  - `tenantId`：多租户ID（你项目必用）
  - `roleCodes`：角色标识集合（少量）
  - `deptId`：部门ID
  <p>
  > 原则：**JWT 只做身份凭证，不做数据容器**
  <p>
  ---
  <p>
  ### 3. Signature 签名规范
  1. 对称加密（单体/内网项目）
  - 使用：`HS256`
  - 密钥：**高强度随机密钥、长度≥32位、环境变量配置、禁止硬编码**
  2. 非对称加密（微服务/对外接口）
  - 私钥签发、公钥验签
  - 算法：`RS256`
  3. 全局约束
  - 禁止关闭签名、禁止弱密钥、禁止固定测试密钥
  <p>
  ---
  <p>
  ## 二、有效期规范（生产最佳实践）
  1. **访问令牌 access-token**
  - 短期有效：**30分钟 ~ 2小时**
  - 减少被盗用风险
  2. **刷新令牌 refresh-token**
  - 长期有效：**7~30天**
  - 用于无感续期，过期强制登录
  3. 关键规则
  - 必须校验 `exp` 过期
  - 服务端**时间校准**，避免时区/时间差导致解析异常
  <p>
  ---
  <p>
  ## 三、传输 & 请求头规范
  1. **请求头固定格式**
  ```http
  Authorization: Bearer ${jwt串}
  ```
  - 固定前缀：`Bearer ` 后面空格 + 完整JWT
  - 禁止自定义头（如 `token: xxx`），遵循 RFC6750 标准
  <p>
  2. 传输约束
  - 生产**强制 HTTPS**，防止抓包劫持
  - 接口返回 Token 统一JSON结构
  ```json
  {
  "code": 200,
  "data": {
  "accessToken": "xxx",
  "refreshToken": "xxx",
  "timeout": 3600
  }
  }
  ```
  <p>
  ---
  <p>
  ## 四、安全强制规范（必遵守）
  1. **禁用敏感字段**
  不存：password、salt、private 隐私数据
  2. **防重放 / 注销方案**
  - 携带 `jti`
  - 退出登录/禁用账号：服务端缓存黑名单（Redis）
  3. **编码规范**
  使用 **Base64URL 安全编码**
  自动忽略 `+ / =` 特殊字符，避免URL、参数解析异常
  4. **跨域 & 前端存储**
  - 优先：`HttpOnly + Cookie` 存储
  - 次选：内存存储
  - 禁止：LocalStorage 长期存储（XSS风险）
  5. **权限控制**
  JWT 不做动态权限判断，**权限实时从接口/缓存读取**
  避免修改权限后，JWT 依旧有效
  <p>
  ---
  <p>
  ## 五、结合你项目（Sa-Token + SpringBoot4 落地规范）
  1. 配置强制项
  - 固定算法：`HS256`
  - 配置过期时间、`jti` 自动生成
  - 密钥配置在 Nacos/环境变量，不写死
  2. 载荷设计参考（你的多租户项目）
  ```json
  {
  "sub": "10001",
  "jti": "rnd-uuid-xxxx",
  "iat": 1712000000,
  "exp": 1712003600,
  "tenantId": "2001",
  "roleCodes": ["admin","user"]
  }
  ```
  3. 校验规则
  - 校验签名合法性
  - 校验过期时间
  - 黑名单拦截（注销用户）
  <p>
  ---
  <p>
  ## 六、禁用反例（绝对不要这么写）
  1. 载荷存放大量业务数据、大对象
  2. 无签名、弱密钥、密钥硬编码
  3. 过期时间设置几天/永久有效
  4. 自定义不标准请求头、不带 Bearer 前缀
  5. JWT 内写死权限、角色，无法动态回收
  <p>
  需要我给你一份 **Sa-Token JWT 标准化配置代码** 直接复制到你项目吗？
 */